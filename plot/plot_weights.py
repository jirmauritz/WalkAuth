import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from matplotlib.colors import Normalize
import numpy as np
import matplotlib.gridspec as gridspec

class MidpointNormalize(Normalize):
    def __init__(self, vmin=None, vmax=None, midpoint=None, clip=False):
        self.midpoint = midpoint
        self.vmax = vmax
        self.vmin = vmin
        Normalize.__init__(self, vmin, vmax, clip)

    def __call__(self, value, clip=None):
        # I'm ignoring masked values and all kinds of edge cases to make a
        # simple example...
        x, y = [self.vmin, self.midpoint, self.vmax], [0, 0.5, 1]
        return np.ma.masked_array(np.interp(value, x, y))

matplotlib.style.use('ggplot')

weights = pd.read_csv('../logs/weights_log.csv', sep=';')
norm = MidpointNormalize(midpoint=0, vmin=weights.min().min(), vmax=weights.max().max())


fig = plt.figure(dpi=900)

gs = gridspec.GridSpec(1,(weights.shape[1] // 3) + 1)

for i in range(0, weights.shape[1] // 3):
    ax = plt.subplot(gs[0,i])
    #print("processing ", weights.iloc[:, (i*3):(i*3)+3]) 
    im = ax.matshow(weights.iloc[:, (i*3):(i*3)+3], norm=norm, cmap=plt.get_cmap("RdGy"))
    plt.axis('off')

ax = plt.subplot(gs[0,weights.shape[1] // 3])
fig.colorbar(im, cax=ax)

plt.savefig('weights.pdf', format='pdf')
#plt.show()
