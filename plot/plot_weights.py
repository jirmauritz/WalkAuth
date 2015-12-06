"""
This script plots final weights of the 1st hidden neuron layer of the trained
neural network. Weights are represented by a colour of gradient.
"""
# library for easy data maniplation
import pandas as pd
# needed for setting plot style
import matplotlib
# library for plotting
import matplotlib.pyplot as plt
# module for colour gradient normalization
from matplotlib.colors import Normalize
# library for mathematics
import numpy as np
# module for plot layout
import matplotlib.gridspec as gridspec

# where to find weights logs
PATH_TO_WEIGHTS_LOG = '../logs/weights_log.csv'
# CSV item separator
SEPARATOR = ';'

"""
This class normalizes the gradient to match value range of the date.
"""
class MidpointNormalize(Normalize):
    def __init__(self, vmin=None, vmax=None, midpoint=None, clip=False):
        self.midpoint = midpoint
        self.vmax = vmax
        self.vmin = vmin
        Normalize.__init__(self, vmin, vmax, clip)

    def __call__(self, value, clip=None):
        x, y = [self.vmin, self.midpoint, self.vmax], [0, 0.5, 1]
        return np.ma.masked_array(np.interp(value, x, y))

# setting visual style to something nicer that default settings (especially
# color scheme)
matplotlib.style.use('ggplot')

# load CSV file
weights = pd.read_csv(PATH_TO_WEIGHTS_LOG, sep=SEPARATOR)
# normalization of coloutgradient according to dataset 
norm = MidpointNormalize(midpoint=0, vmin=weights.min().min(), vmax=weights.max().max())

# create new figure with given DPI
fig = plt.figure(dpi=900)
# prepare grid of plots with one row and column for every neuron + 1 for
# gradient scale
gs = gridspec.GridSpec(1,(weights.shape[1] // 3) + 1)

# plot every neuron
for i in range(0, weights.shape[1] // 3):
    # create subplot in the given cell
    ax = plt.subplot(gs[0,i])
    # plot the weights with given normalized color gradient
    im = ax.matshow(weights.iloc[:, (i*3):(i*3)+3], norm=norm, cmap=plt.get_cmap("RdGy"))
    # do not plot axes 
    plt.axis('off')

# create the last subplot for gradient scale
ax = plt.subplot(gs[0,weights.shape[1] // 3])
# plot the colorbar
fig.colorbar(im, cax=ax)

# save the whole figure into given file
plt.savefig('weights.pdf', format='pdf')

#plt.show()
