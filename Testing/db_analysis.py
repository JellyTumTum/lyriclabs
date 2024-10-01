from sqlalchemy import create_engine
import pandas as pd
from scipy import stats
from scipy.stats import shapiro, levene
from scipy.stats import ttest_ind
import matplotlib.pyplot as plt

engine = create_engine('postgresql+psycopg2://postgres:password@192.168.1.159:5432/project')


try:
    df_users = pd.read_sql("SELECT * FROM users;", engine)
    df_room_archive = pd.read_sql("SELECT * FROM room_archive;", engine)
    df_artist = pd.read_sql("SELECT * FROM artist;", engine)
    df_user_guess = pd.read_sql("SELECT * FROM user_guess;", engine)
    df_practice_guess = pd.read_sql("SELECT * FROM practice_guess;", engine)

except Exception as e:
    print(e)


# Splitting the concatenated strings into lists
df_room_archive['userIDList'] = df_room_archive['user_id_list'].str.split(',')
df_room_archive['artistIDList'] = df_room_archive['artist_id_list'].str.split(',')

# Separate dataframes for practice and non-practice games
practice_df = df_room_archive[df_room_archive['is_practice']]
non_practice_df = df_room_archive[~df_room_archive['is_practice']]

# Flatten the lists to get a series of user IDs for each category
practice_user_ids = practice_df['userIDList'].explode()
non_practice_user_ids = non_practice_df['userIDList'].explode()

# Get unique user IDs
unique_practice_user_ids = practice_user_ids.unique()
unique_non_practice_user_ids = non_practice_user_ids.unique()

# Count of unique user IDs
count_unique_practice_user_ids = len(unique_practice_user_ids)
count_unique_non_practice_user_ids = len(unique_non_practice_user_ids)

# User IDs in both practice and non-practice games
user_ids_in_both = set(unique_practice_user_ids) & set(unique_non_practice_user_ids)
count_user_ids_in_both = len(user_ids_in_both)


# Outputting the results
print(f"Unique user IDs in practice games: {count_unique_practice_user_ids}")
print(f"Unique user IDs in non-practice games: {count_unique_non_practice_user_ids}")
print(f"User IDs in both: {count_user_ids_in_both}")

# Calculate the number of lobbies for each max_guess_time from 5 to 15
lobby_counts_by_time = df_room_archive['max_guess_time'].value_counts().sort_index()

# Filter out the counts for max_guess_time values from 5 to 15
filtered_lobby_counts_by_time = lobby_counts_by_time.loc[5:15]

# Merge room_archive with user_guess to link each guess with its respective room's max_guess_time
merged_df = pd.merge(df_user_guess, df_room_archive[['room_id', 'max_guess_time']], left_on='roomid', right_on='room_id')

# Merge room_archive with user_guess to link each guess with its respective room's max_guess_time
merged_df_player_count = pd.merge(df_user_guess, df_room_archive[['room_id', 'player_count']], left_on='roomid', right_on='room_id')

# Group by max_guess_time and count the number of guesses in each group
guesses_counts_by_time = merged_df.groupby('max_guess_time').size()

# Filter out the counts for max_guess_time values from 5 to 15
filtered_guesses_counts_by_time = guesses_counts_by_time.loc[5:15]

# # Outputting the counts for each max_guess_time
# print("Number of guesses for each max_guess_time (from 5 to 15):")
# print(filtered_guesses_counts_by_time)


# # Outputting the counts for each max_guess_time
# print("Number of lobbies with each max_guess_time (from 5 to 15):")
# print(filtered_lobby_counts_by_time)


# Calculate the number of lobbies for each player count
lobby_counts_by_player = df_room_archive['player_count'].value_counts().sort_index()

# Outputting the counts for each player count
print("Number of lobbies with each player count:")
print(lobby_counts_by_player)

# Group by player_count and count the number of guesses in each group
guesses_counts_by_player = merged_df_player_count.groupby('player_count').size()

# Outputting the counts for player counts
print("Number of guesses for each player count:")
print(guesses_counts_by_player)

# Ensure 'total_guess_time' is in the appropriate unit (e.g., seconds) in 'df_user_guess'
df_user_guess['total_guess_time_sec'] = df_user_guess['total_guess_time'] / 1000

# Merge 'df_room_archive' with 'df_user_guess' to associate each guess with its room's player count
merged_df = pd.merge(df_user_guess, df_room_archive[['room_id', 'player_count']], left_on='roomid', right_on='room_id')

# Calculate the average total guess time for each room
avg_guess_time_per_room = merged_df.groupby(['roomid', 'player_count'])['total_guess_time_sec'].mean().reset_index()

# Now calculate the average of these averages for each player count
avg_guess_time_by_player_count = avg_guess_time_per_room.groupby('player_count')['total_guess_time_sec'].mean()

# Calculate the variance of the average guess times across different player counts
variance_of_avg_guess_times = avg_guess_time_by_player_count.var()

print("Average guess times by player count:")
print(avg_guess_time_by_player_count)

print("\nVariance of average guess times across player counts:", variance_of_avg_guess_times)


avg_guess_time_by_player_count.plot(kind='bar', color='skyblue')
plt.xlabel('Player Count')
plt.ylabel('Average Total Guess Time (sec)')
plt.title('Average Guess Time by Player Count')
plt.show()