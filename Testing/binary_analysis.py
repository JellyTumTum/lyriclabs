from sqlalchemy import create_engine
import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import chi2_contingency

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

# Perform a t-test
t_stat, p_val = ttest_ind(df_practice_guess['total_guess_time'], df_user_guess['total_guess_time'])

print(f"T-statistic: {t_stat}, p-value: {p_val}")

# Creating a contingency table
# Counting correct and incorrect guesses in both datasets
practice_correct = df_practice_guess['correct_guess'].sum()
practice_total = len(df_practice_guess)
practice_incorrect = practice_total - practice_correct

user_correct = df_user_guess['correct_guess'].sum()
user_total = len(df_user_guess)
user_incorrect = user_total - user_correct

contingency_table = [[practice_correct, practice_incorrect], [user_correct, user_incorrect]]

chi2, p, dof, expected = chi2_contingency(contingency_table)

print(f"Chi-squared: {chi2}, p-value: {p}")

