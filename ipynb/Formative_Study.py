import csv
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib

import seaborn as sns
sns.set_context(
    "talk",
    font_scale=1,
    rc={
        "lines.linewidth": 2,
        "text.usetex": True,
        "font.family": 'serif',
        "font.serif": ['Palatino'],
        "font.size": 18
    })
sns.set_style('ticks')
sns.set_style({"ytick.direction": "in", "ytick.major.size": 6.0,  "ytick.minor.size": 3.0, "xtick.direction": "in", "xtick.major.size": 0.0,  "xtick.minor.size": 0.0})

from matplotlib import rc
font = {'family': 'serif', 'serif': ['Palatino'], 'size': 18}
rc('font', **font)

plt.rcParams["figure.figsize"] = (18, 6)


plt.clf()

P = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
V = [2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
X = ['Yes', 'No', 'Yes', 'No','Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No','Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No', 'Yes', 'No']
Y = [65, 35, 40, 60, 70, 30, 60, 40, 48, 52, 45, 55, 47, 53, 55, 45, 40, 60, 60, 40, 48.6, 51.4, 55, 45, 40, 60, 58.6, 41.4, 50, 50, 48.6, 51.4, 52, 48, 40, 60]


df = pd.DataFrame(
    {   'P': P,
        'V': V,
        'X': X,
        'Y': Y
    })

colors = ["#9ecae1", "#fc9272", "#a1d99b"]

g = sns.FacetGrid(df, col ='P', row = 'V', sharey = False, sharex = False)
g.map(sns.barplot, 'X', 'Y', errwidth = 1.0, capsize = 0.1, palette=sns.color_palette(colors))

g.fig.subplots_adjust(wspace = 0, hspace = 0)

g.axes[0, 0].set_ylabel('G1')
g.axes[1, 0].set_ylabel('G2')

g.axes[0, 0].tick_params(labelbottom='off')
g.axes[0, 1].tick_params(labelbottom='off')
g.axes[1, 0].tick_params(labelbottom='off')
g.axes[1, 1].tick_params(labelbottom='off') 

g.axes[1, 0].set_xlabel('')
g.axes[1, 1].set_xlabel('')


g.axes[0, 0].set_title('P1 [48.6 : 51.4]')
g.axes[0, 1].set_title('P2 [70.2 : 29.8]')
g.axes[1, 0].set_title('P2 [70.2 : 29.8]')
g.axes[1, 1].set_title('P1 [48.6 : 51.4]')

major_yticks = np.arange(0, 105, 20)
minor_yticks = np.arange(0, 105, 5)
axes_count = 0

for i, ax in enumerate(g.axes[-1, :]):
    rects = ax.patches
    for j, rect in enumerate(rects):
        height = rect.get_height()
        ax.text(rect.get_x() + rect.get_width()/2., 0.05*height, '%.2f' % height, ha='center', va='bottom', fontsize=16)
        ax.plot([-0.4, 0.4], [50.8, 50.8], 'k--',  dashes=(3, 5), linewidth = 0.9)
        ax.plot([0.6, 1.4], [49.2, 49.2], 'k--',  dashes=(3, 5), linewidth = 0.9)
    ax.set_yticks(major_yticks)
    ax.set_yticks(minor_yticks, minor = True)

for i, ax in enumerate(g.axes[-2, :]):
    rects = ax.patches
    for j, rect in enumerate(rects):
        height = rect.get_height()
        ax.text(rect.get_x() + rect.get_width()/2., 0.05*height, '%.2f' % height, ha='center', va='bottom', fontsize=16)
        ax.plot([-0.4, 0.4], [50.8, 50.8], 'k--', dashes=(3, 5), linewidth = 0.9)
        ax.plot([0.6, 1.4], [49.2, 49.2], 'k--', dashes=(3, 5), linewidth = 0.9)
    ax.set_yticks(major_yticks)
    ax.set_yticks(minor_yticks, minor = True)


#sns.despine(bottom = True)
plt.tight_layout()
plt.savefig('Formative_Study.pdf', bbox_inches='tight')
plt.close()

