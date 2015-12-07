"""
This script plots progress of learning of neural network.
"""
# library for easy data manipulation
import pandas as pd
# needed for setting plot style
import matplotlib
# library for plotting
import matplotlib.pyplot as plt

# where to find learning logs
PATH_TO_LEARNING_LOG = '../logs/learning_log.csv'
# CSV item separator
SEPARATOR = ';'
# setting visual style to something nicer that default settings (especially
# color scheme)
matplotlib.style.use('ggplot')

# load CSV file
LEARNING = pd.read_csv(PATH_TO_LEARNING_LOG, sep=SEPARATOR)

# plot accuracies
LEARNING[["validation accuracy", "training accuracy"]].plot()
plt.savefig('learning_accuracy.pdf', format='pdf')

# plot errors
LEARNING[["validation error", "training error"]].plot()
plt.savefig('learning_error.pdf', format='pdf')

# plot RMSEs
LEARNING[["validation RMSE", "training RMSE"]].plot()
plt.savefig('learning_rmse.pdf', format='pdf')

# plot F1s
LEARNING[["validation F1", "training F1"]].plot()
plt.savefig('learning_f1.pdf', format='pdf')

#plt.show()
