import pandas as pd
import matplotlib
import matplotlib.pyplot as plt

matplotlib.style.use('ggplot')

learning = pd.read_csv('../logs/learning_log.csv', sep=';')

learning[["validation error", "training error"]].plot()
plt.savefig('learning_error.pdf', format='pdf')
learning[["validation RMSE", "training RMSE"]].plot()
plt.savefig('learning_rmse.pdf', format='pdf')
learning[["validation F1", "training F1"]].plot()
plt.savefig('learning_f1.pdf', format='pdf')

#plt.show()
