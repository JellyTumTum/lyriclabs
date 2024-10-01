from sqlalchemy import create_engine
import pandas as pd
from scipy import stats
from scipy.stats import shapiro, levene
import statsmodels.api as sm
from statsmodels.stats.diagnostic import het_breuschpagan

# Replace '192.168.1.XXX' with the IP address of the PostgreSQL server within your local network
engine = create_engine('postgresql+psycopg2://postgres:password@192.168.1.159:5432/project')


try:
    df_users = pd.read_sql("SELECT * FROM users;", engine)
    df_room_archive = pd.read_sql("SELECT * FROM room_archive;", engine)
    df_artist = pd.read_sql("SELECT * FROM artist;", engine)
    df_user_guess = pd.read_sql("SELECT * FROM user_guess;", engine)
    df_practice_guess = pd.read_sql("SELECT * FROM practice_guess;", engine)

except Exception as e:
    print(e)


df_user_guess.rename(columns={'roomid': 'room_id'}, inplace=True)
df_analysis = pd.merge(df_user_guess, df_room_archive[['room_id', 'player_count', 'max_guess_time']], on='room_id')

# Convert 'total_guess_time' from milliseconds to seconds for readability
df_analysis['total_guess_time_sec'] = df_analysis['total_guess_time'] / 1000

# Filter for first guesses
first_guesses = df_analysis[df_analysis['guess_count'] == 1]

# Filter for correct guesses
correct_guesses = df_analysis[df_analysis['correct_guess'] == True]

def perform_multiple_regression(df, type):
    # Prepare independent variables
    X = df[['player_count', 'max_guess_time']]
    X = sm.add_constant(X)  # Adding a constant (intercept) to the model
    
    # Dependent variable
    Y = df['total_guess_time_sec']
    
    # Fit the model
    model = sm.OLS(Y, X).fit()
    bp_test = het_breuschpagan(model.resid, model.model.exog)
    
    # Print results
    print(f"Multiple Regression Analysis for {type}")
    print(model.summary())
    print("\nBreusch-Pagan test statistics:")
    print(f"Lagrange Multiplier statistic: {bp_test[0]}")
    print(f"p-value: {bp_test[1]}")
    print(f"f-value: {bp_test[2]}")
    print(f"f p-value: {bp_test[3]}\n")

# Ensure 'max_guess_time' is included in 'df_analysis' before running the function
df_analysis = pd.merge(df_analysis, df_room_archive[['room_id', 'max_guess_time']], on='room_id')

# Perform multiple regression analysis for first and correct guesses
perform_multiple_regression(first_guesses, "First Guesses")
perform_multiple_regression(correct_guesses, "Correct Guesses")
