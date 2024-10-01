import pandas as pd
from statistics import mean

data = {
    "labels": [
        "SlicedBread",
        "JellyTumTum",
        "BarryAllen",
        "MonetaryMitch",
        "willrich16",
        "GeckoCakes",
        "hguest97",
        "juxtaj",
        "BurntToast",
        "Coley03",
        "bxnji",
        "DracoMalfoy",
        "SlicedBread",
        "JellyTumTum",
        "BarryAllen",
        "MonetaryMitch",
        "willrich16",
        "GeckoCakes",
        "hguest97",
        "juxtaj",
        "BurntToast",
        "Coley03",
        "bxnji",
        "DracoMalfoy"
    ],
    "datasets": [
        {
            "label": "Correct Guess (Multiplayer, Practice)",
            "data": [
                {
                    "x": 6.557307692307693,
                    "y": 2.523888888888889,
                    "roomID": "SlicedBread"
                },
                {
                    "x": 4.200083333333333,
                    "y": 2.64428125,
                    "roomID": "JellyTumTum"
                },
                {
                    "x": 5.524071428571429,
                    "y": 3.360695652173913,
                    "roomID": "BarryAllen"
                },
                {
                    "x": 2.4974545454545454,
                    "y": 2.561,
                    "roomID": "MonetaryMitch"
                },
                {
                    "x": 5.366867924528302,
                    "y": 5.641375,
                    "roomID": "willrich16"
                },
                {
                    "x": 4.70222,
                    "y": 4.27075,
                    "roomID": "GeckoCakes"
                },
                {
                    "x": 4.9013846153846155,
                    "y": 5.095357142857142,
                    "roomID": "hguest97"
                },
                {
                    "x": 3.87475,
                    "y": 1.2803333333333333,
                    "roomID": "juxtaj"
                },
                {
                    "x": 7.262785714285714,
                    "y": 5.194,
                    "roomID": "BurntToast"
                },
                {
                    "x": 8.47056,
                    "y": 5.010384615384615,
                    "roomID": "Coley03"
                },
                {
                    "x": 6.659115384615385,
                    "y": 7.7908,
                    "roomID": "bxnji"
                },
                {
                    "x": 9.4428,
                    "y": 7.154083333333333,
                    "roomID": "DracoMalfoy"
                }
            ],
            "backgroundColor": "#6DD47E"
        },
        {
            "label": "First Guess (Multiplayer, Practice)",
            "data": [
                {
                    "x": 3.3304761904761904,
                    "y": 2.523888888888889,
                    "roomID": "SlicedBread"
                },
                {
                    "x": 3.600232704402516,
                    "y": 2.64428125,
                    "roomID": "JellyTumTum"
                },
                {
                    "x": 4.510787234042553,
                    "y": 3.360695652173913,
                    "roomID": "BarryAllen"
                },
                {
                    "x": 2.2853684210526315,
                    "y": 2.561,
                    "roomID": "MonetaryMitch"
                },
                {
                    "x": 4.417203125,
                    "y": 5.641375,
                    "roomID": "willrich16"
                },
                {
                    "x": 3.9846666666666666,
                    "y": 4.27075,
                    "roomID": "GeckoCakes"
                },
                {
                    "x": 3.2070444444444446,
                    "y": 5.095357142857142,
                    "roomID": "hguest97"
                },
                {
                    "x": 1.6303191489361701,
                    "y": 1.2803333333333333,
                    "roomID": "juxtaj"
                },
                {
                    "x": 5.110130434782609,
                    "y": 5.194,
                    "roomID": "BurntToast"
                },
                {
                    "x": 6.570515151515152,
                    "y": 5.010384615384615,
                    "roomID": "Coley03"
                },
                {
                    "x": 4.633740740740741,
                    "y": 7.7908,
                    "roomID": "bxnji"
                },
                {
                    "x": 7.631181818181818,
                    "y": 7.154083333333333,
                    "roomID": "DracoMalfoy"
                }
            ],
            "backgroundColor": "#4CB3D4"
        }
    ]
}

# Convert the data points to DataFrames
df_correct_guess = pd.DataFrame(data['datasets'][0]['data'])
df_first_guess = pd.DataFrame(data['datasets'][1]['data'])

# Adding labels to the dataframes
df_correct_guess['label'] = data['labels']
df_first_guess['label'] = data['labels']

# Merge the two DataFrames on roomID
df_merged = pd.merge(df_correct_guess, df_first_guess, on="roomID", suffixes=('_correct', '_first'))

# Calculate differences in 'x' values
df_merged['x_difference'] = df_merged['x_correct'] - df_merged['x_first']
df_merged['percentage_difference'] = (df_merged['x_difference'] / df_merged['y_correct']) * 100

# Find the entries with the lowest and highest percentage difference
lowest_percentage_diff_entry = df_merged.loc[df_merged['percentage_difference'].idxmin()]
highest_percentage_diff_entry = df_merged.loc[df_merged['percentage_difference'].idxmax()]

# Print the results including the related username/label
print(f"Lowest percentage difference: {lowest_percentage_diff_entry['percentage_difference']}% for {lowest_percentage_diff_entry['label']}")
print(f"Highest percentage difference: {highest_percentage_diff_entry['percentage_difference']}% for {highest_percentage_diff_entry['label']}")
print(f"Mean percentage difference: {df_merged['percentage_difference'].mean()}%")