
import networkx as nx
import json
from barchart import *
import base64
# 	Lattice is a DAG (graph) of Node objects
class Lattice:

    # contain a graph of Node objects
    def __init__(self):
        self.graph = nx.DiGraph()
        self.type = 'summarized' #'full' or summarized
        self.nodeDic = {}
        
    def generateDashboard(self):
        raise NotImplementedError
    def setNodeDic(self,nodeDic):
        self.nodeDic = nodeDic
    def getNodeDic(self):
        return self.nodeDic 
    # populate a lattice of given visualizations to test rendering functions
    def populateDummyLattice(self):
        raise NotImplementedError

    def __repr__(self):
        return '(Nodes: %s, numberOfNodes: %s)' % (self.getNodes(), self.numberOfNodes())


    def addNode(self, node):
        # wrapper function for adding a node
        node.id = self.numberOfNodes() + 1
        
        if node.parents==None: #Root
            self.graph.add_node(node,filters=None,id=node.id)    
        else:
            self.graph.add_node(node,filters=str(list(node.filters)),id=node.id)
            for parent in node.parents:
                self.addEdge(parent, node)

    def addMultiNodes(self, list):
        # wrapper function for adding multiple nodes
        for node in list:
            self.addNode(node)

    def addNodePlus(self, node_name, attribute):
        # wrapper function for adding a node with (k,v) attribute
        # [(race,black), (age,15), (gender,female)]
        for (k,v) in attribute:
            self.graph.add_node(node_name,k=v)

    def deleteNode(self, node_name):
        # wrapper function for deleting a node
        self.graph.remove_node(node_name)


    def addEdge(self, head, tail):
        # wrapper function for adding an edge
        self.graph.add_edge(head, tail)

    def addEdgePlus(self, head, tail, attribute):
        # wrapper function for adding an Edge with (k,v) attribute
        # [(race,black), (age,15), (gender,female)]
        for (k, v) in attribute:
            self.graph.add_edge(head, tail, k=v)

    def deleteEdge(self, head, tail):
        # wrapper function for deleting an Edge
        self.graph.remove_edge(head, tail)

    def getNodes(self):
        return self.graph.nodes()

    def getEdges(self):
        return self.graph.edges()

    def numberOfNodes(self):
        return self.graph.number_of_nodes()

    def numberOfEdges(self):
        return self.graph.number_of_edges()

    def generateNodeDic(self):
        # Generates a Node dictionary
        # containing index and node object: {0: node0, 1: node2}
        node_dic = {}
        for node in self.getNodes():
            each = []
            viz = node.get_viz()
            # current = viz[0]
            current=viz
            for idx, val in enumerate(current.X):
                each.append({"xAxis": val, "yAxis": current.data[idx]})
            each.append({"filter": ' '.join(current.filters), "yName": current.Y, "childrenIndex": node.childrenIndex})
            node_dic[node.id] = each
        self.setNodeDic(node_dic)
        return node_dic

    def drawGraphToPNG(self):
        G = self.graph
        # print(G.numberOfEdges())
        p = nx.drawing.nx_pydot.to_pydot(G)
        p.write_png('graph.png')

    def generateNode(self, node_dic):
        node = []
        nodes = list(node_dic.values())
        barcharts = []
        print nodes
        for i in nodes:
            yVals = []
            for values in i:
                try:
                    yVals.append(values['yAxis'])
                except KeyError:
                    pass
            xAttrs = []
            for values in i:
                try:
                    xAttrs.append(values['xAxis'])
                except KeyError:
                    pass
            if values["filter"]=="#":
                filterVal="root"
            else:
                filterVal = str(values["filter"][1:-1].replace("#",",\n").replace("$","="))
            barcharts.append(bar_chart(yVals, xAttrs, xtitle="", ytitle="", title=filterVal, top_right_text="", N=1, width=0.1))

        barcharts_new = []
        for i in barcharts:
            barcharts_new.append(base64.b64encode(i))

        for i in range(len(nodes)):
            node.append(i+1)
            node.append(barcharts_new[i])
        return node

    def generateEdge(self, node_dic):
        edge = []
        if len(node_dic)>0:
            nBars = len(node_dic.values()[0])-1
            for key in node_dic.keys():
                # if len(node_dic[key][nBars])>2: #some childrenIndex might be empty (avoid indexing these otherwise keyerror)
                for i in node_dic[key][nBars]['childrenIndex']: #index [3] need to generalize to things with more than 2 bars
                    edge.append(key)
                    edge.append(i)
        return edge

    def generateSvg(self, node_dic):
        return None

