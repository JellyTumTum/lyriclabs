from sqlalchemy import create_engine
import pandas as pd
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

# Preparing the data by merging
df_analysis = pd.merge(df_user_guess, df_room_archive[['room_id', 'player_count']], left_on='roomid', right_on='room_id')

# Convert 'total_guess_time' from milliseconds to seconds for readability
df_analysis['total_guess_time_sec'] = df_analysis['total_guess_time'] / 1000

# Filter for first guesses and correct guesses
first_guesses = df_analysis[df_analysis['guess_count'] == 1]
correct_guesses = df_analysis[df_analysis['correct_guess'] == True]

def perform_gls(df, type):
    X = df[['player_count']]  # Independent variable
    Y = df['total_guess_time_sec']  # Dependent variable
    
    X = sm.add_constant(X)  # Adding a constant (intercept) to the model
    model = sm.GLS(Y, X).fit()  # Fit model using GLS instead of OLS
    
    print(f"Generalized Least Squares Analysis for {type}")
    print(model.summary())
    print("\n\n")

# Perform GLS analysis for first guesses
perform_gls(first_guesses, "First Guesses")

# Perform GLS analysis for correct guesses
perform_gls(correct_guesses, "Correct Guesses")