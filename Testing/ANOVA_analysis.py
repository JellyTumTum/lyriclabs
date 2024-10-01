from sqlalchemy import create_engine
import pandas as pd
from scipy import stats
from scipy.stats import shapiro, levene

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


# Normality Check
def check_normality(groups):
    stats = []
    p_values = []
    for key, values in groups.items():
        stat, p = shapiro(values)
        stats.append(stat)
        p_values.append(p)
        # print(f"Group {key} - Shapiro-Wilk test statistic: {stat}, p-value: {p}")
    
    print(f"Lowest Shapiro-Wilk statistic: {min(stats)}, Highest: {max(stats)}, Average: {sum(stats)/len(stats)}")
    print(f"Lowest p-value: {min(p_values)}, Highest: {max(p_values)}, Average: {sum(p_values)/len(p_values)}")

# Homogeneity of Variances Check
def check_homogeneity(groups):
     # Extracting all lists of group values directly from the Series object
    all_values = groups.tolist()

    # Apply Levene's test across all groups
    stat, p = levene(*all_values)
    print(f"Levene's test for homogeneity of variances: Stat={stat}, p={p}")


# Splitting the concatenated strings into lists
df_room_archive['userIDList'] = df_room_archive['user_id_list'].str.split(',')
df_room_archive['artistIDList'] = df_room_archive['artist_id_list'].str.split(',')

# Merging to link max_guess_time with each guess
df_analysis = pd.merge(df_user_guess, df_room_archive[['room_id', 'max_guess_time']], left_on='roomid', right_on='room_id')

# df_analysis['total_guess_time'] = pd.to_numeric(df_analysis['total_guess_time'])
# Convert max_guess_time to milliseconds for consistency
df_analysis['max_guess_time_ms'] = df_analysis['max_guess_time'] * 1000

# Calculate response time as a percentage of the max guess time
df_analysis['response_time_percentage'] = (df_analysis['total_guess_time'] / df_analysis['max_guess_time_ms']) * 100

# Filter for first guesses and correct guesses
first_guesses = df_analysis[df_analysis['guess_count'] == 1]
correct_guesses = df_analysis[df_analysis['correct_guess'] == True]

# Group by max_guess_time and collect response_time_percentage
groups_first_guesses = first_guesses.groupby('max_guess_time')['response_time_percentage'].apply(list)
groups_correct_guesses = correct_guesses.groupby('max_guess_time')['response_time_percentage'].apply(list)

# Perform ANOVA using response_time_percentage
anova_result_first = stats.f_oneway(*groups_first_guesses)
anova_result_correct = stats.f_oneway(*groups_correct_guesses)

print(f"First Guess Percentage ANOVA F-statistic: {anova_result_first.statistic}, p-value: {anova_result_first.pvalue}")
print(f"Correct Guess Percentage ANOVA F-statistic: {anova_result_correct.statistic}, p-value: {anova_result_correct.pvalue}")

print("First Guesses Normality Check")
check_normality(groups_first_guesses)

print("\nCorrect Guesses Normality Check")
check_normality(groups_correct_guesses)

print("Checking homogeneity for first guesses")
check_homogeneity(groups_first_guesses)

print("Checking homogeneity for correct guesses")
check_homogeneity(groups_correct_guesses)


