import pandas as pd
import matplotlib
import matplotlib.pyplot as plt

matplotlib.style.use('ggplot')

learning = pd.read_csv('../logs/learning_log.csv')

plt.style.context('fivethirtyeight')

learning[["validation error", "training error"]].plot()
learning[["validation RMSE", "training RMSE"]].plot()
learning[["validation F1", "training F1"]].plot()

plt.show()
