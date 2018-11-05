import pandas as pd
import matplotlib.pyplot as plt

def single_bar_chart(A,B, N=1,width=0.3,title="",subpopulation_name="",vars_of_interest=('M','F')):
    
    ind = np.arange(N)  # the x locations for the groups
    
    fig, ax = plt.subplots()
    rects1 = ax.bar(ind, A, width, color='b', ecolor= "black")
    rects2 = ax.bar(ind + width, B, width, color='r', ecolor= "black")
    # add some text for labels, title and axes ticks
    ax.set_title(title,fontsize=16)
    ax.set_xticks([width])
    ax.set_xticklabels((''),fontsize=14)
    ax.set_ylim(0,100)
    ax.set_ylabel('%',fontsize=14)
    ax.set_title(subpopulation_name,fontsize=16)
    #ax.set_yticklabels(vars_of_interest)
    #plt.gca().set_xticks([-0.1,0.1])
    ax.set_xticks([0,0.1])
    ax.set_xticklabels(['M','F'],fontsize=14)
    ax.legend((rects1[0], rects2[0]), vars_of_interest,fontsize=12)
#     ax.set_yticklabels(np.arange(0,110,10),fontsize=13)
#     ax.legend((rects1[0], rects2[0]), ('A', 'B'))
    def autolabel(rects):
        # attach some text labels
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/2., 0.5*height,
                    '%.2f' % float(height),
                    ha='center', va='bottom',color="white",fontweight='bold',fontsize=15)

    autolabel(rects1)
    autolabel(rects2)
    #ax.set_xlim(-0.1,0.2)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)

def subpopulation_viz(data,condition,subpopulation_name):
    if condition is not None: 
        subset_data = data[condition].mean()[population_of_interest]
        result =  subset_data/sum(subset_data)*100
        single_bar_chart(result[0],result[1],width=0.1,subpopulation_name=subpopulation_name)
    else:
        single_bar_chart(0,0,width=0.1,subpopulation_name=subpopulation_name)