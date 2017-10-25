# This file contains code that constructs the visualization node and lattices
from database import *
from vizObj import *
from node import *
from lattice import *

import json
import string
import math
import matplotlib.pyplot as plt
import numpy as np
import StringIO
def generateVizObj(tablename,x_attr,y_attr, agg_func, filters):
	v = vizObj()
	# 	summarization=# SELECT survived, COUNT(id)  FROM titanic WHERE sex='male' AND age<20 GROUP BY survived;
	#  survived | count
	# ----------+-------
	#         0 |    46
	#         1 |    27
	# (2 rows)
	(xVals, yVals) = query_vizData(tablename,x_attr,y_attr, agg_func, filters)
	v.setX(xVals)
	v.setData(yVals) 
	v.setY(agg_func+'('+y_attr+')') 
	v.setFilters(filters)
	v.setAgg(agg_func)
	return v  

def buildCondition(attribute,value):
    return str(attribute +"= '"+ value+"'")

def level(nodeFilter):
    # determine which level a filter lies in
    return len(nodeFilter) - nodeFilter.count('*')
def arr2List(lst):
    return [list(_x) for _x in lst]
def findNodeWithFilter(G,filter):
    # return the node with that filter
    for vnode, val in G.graph.node.iteritems():
        if val["filters"]==filter:
            return vnode
import itertools
from itertools import combinations
def generateLattice(xAttr,yAttr,aggrFunc,attributes,tablename,MAX_DEPTH=-1,DEBUG=False):
    '''
    Given a list of categorical attributes, automatically generate 
    lattice structure with appropriate parent child relationship. 
    '''
    if MAX_DEPTH==-1:
        MAX_DEPTH=len(attributes)
    assert MAX_DEPTH<=len(attributes)

    #1. determine all possible filters from given attribute names
    filters = {}
    for attr in attributes: 
        filters.update(findDistinctAttrVal(attr,tablename))
    conditions = [[] for _i in filters.keys()]
    for i,attr in enumerate(filters.keys()):
        for val in filters[attr]:
            conditions[i].append(buildCondition(attr,val))
        conditions[i].append("*")
    # 2. Generate all possible filter combinations
    node_filters=[]
    for combination in itertools.product(*conditions):
        node_filters.append(combination)   
    node_filters_levels = np.array([level(nf) for nf in node_filters])
    # Cleaning out "*" for "all"
    node_filters = np.array([list(filter(lambda x: x != '*', a)) for a in node_filters])
    # 3. Assign parent child relationships of each node (corresponding to a filter) to generate the lattice
    # Base Case: Starting from root --> level 1
    idx = np.where(node_filters_levels==1)[0]
    prev_level_filters=node_filters[idx]
    G = Lattice()
    vobj = vizObj(x=xAttr,y=yAttr,filters=None,agg_func='COUNT',tablename=tablename)
    root = vizNode(vobj,None)
    G.addNode(root)
    for nf in node_filters[idx]:
        vobj= vizObj(x=xAttr,y=yAttr,filters=list(nf),agg_func='COUNT',tablename=tablename)
        vnode = vizNode(viz=vobj,parents=[root])
        G.addNode(vnode)

    if DEBUG: 
        plt.figure()
        plt.title("Level 1")
        nx.draw(G.graph)
        plt.savefig("graph1.png")
    # Level n >2 
    for level_i in range(2,MAX_DEPTH+1):
        idx = np.where(node_filters_levels==level_i)[0]
        level_n_filters=  node_filters[idx]
        for nf in level_n_filters:
            vobj= vizObj(x=xAttr,y=yAttr,filters=nf,agg_func='COUNT',tablename=tablename)
            parent_lst = []
            for f in nf:
                if f!="*":
                    parent_idx = np.where([f in x for x in  prev_level_filters ])[0]
                    parent_nodes = [findNodeWithFilter(G,str(f)) for f in prev_level_filters[parent_idx]]
                    parent_lst.extend(parent_nodes)
            vnode = vizNode(viz=vobj,parents=parent_lst)
            G.addNode(vnode)
        if DEBUG:
            plt.figure()
            plt.title("Level  {}".format(level_i))
            nx.draw(G.graph)
            plt.savefig("graph{}.png".format(level_i))
    return G 
def millify(n):
    n = float(n)
    millidx = max(0,min(len(millnames)-1,
                        int(math.floor(0 if n == 0 else math.log10(abs(n))/3))))
    if millidx<2:
        return int(n)
    else:
        return '{:.0f}{}'.format(n / 10**(3 * millidx), millnames[millidx])
def get_cmap(n, name='hsv'):
    '''Returns a function that maps each index in 0, 1, ..., n-1 to a distinct 
    RGB color; the keyword argument name must be a standard mpl colormap name.'''
    return plt.cm.get_cmap(name, n)
def autolabel(rects, ax):
    # Get y-axis height to calculate label position from.
    (y_bottom, y_top) = ax.get_ylim()
    y_height = y_top - y_bottom

    for rect in rects:
        height = rect.get_height()
        label_position = height + (y_height * 0.01)

        ax.text(rect.get_x() + rect.get_width()/2., label_position-(height/2),
                '%.1f' % height,
                ha='center', va='bottom',fontsize=11)

def bar_chart(yVals,xAttrs,xtitle="",ytitle="",title="",top_right_text="", N=1,width=0.1):
    ind = np.arange(N)  # the x locations for the groups
    fig, ax = plt.subplots(figsize=(2,2))
    cmap = get_cmap(len(yVals)+1)
    rects = []
    for i in range(len(yVals)):
        rect = ax.bar(ind + i*width, yVals[i], width, color=cmap(i),  ecolor= "black")
        rects.append(rect)


    ax.set_xlabel(xtitle,fontsize=14)
    ax.set_ylabel(ytitle,fontsize=14)
    title = ax.set_title(title,fontsize=12)
    
    # Left vertical title
    #title.set_position((1.1,0.9))
    #title.set_rotation(270)

    xmin = -0.05
    xmax = 0.25+0.1*(len(yVals)-2)
    xtickpos = [np.abs(xmin-xmax)/(len(yVals)+2)*(i+0.7) for i in range(len(yVals))]
    ax.set_xticks(xtickpos)
    ax.set_xticklabels(xAttrs,fontsize=12)
    #ax.set_xlabel(xtitle,fontsize=12)

    #ax.legend((rects1[0], rects2[0]), xAttrs)
    ax.annotate(top_right_text, xy=(0.75, 0.85), xycoords='axes fraction')
    ax.set_xlim(xmin,xmax)
    ax.set_ylim((0,100))

    for rect in rects:
        autolabel(rect, ax)
    plt.tight_layout()
    # save as svg string 
    imgdata = StringIO.StringIO()
    fig.savefig(imgdata, format='svg')
    imgdata.seek(0)  # rewind the data
    svg_str = imgdata.buf  # this is svg data
    return svg_str
if __name__=="__main__":
    # attributes = ['cap_surface','cap_color','bruises']
    # G = generateLattice('type','*','COUNT',attributes,'mushroom',DEBUG=True)
    data = bar_chart([70,30],["M","F"],"Gender","COUNT(id)")
    print data
