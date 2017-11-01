
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
        return node_dic


    def generateNodeDicJsonFile(self):
        node_dic = {}
        for node in self.getNodes():
            each = []
            viz = node.get_viz()
            # current = viz[0]
            current=viz
            for idx, val in enumerate(current.X):
                each.append({"xAxis": val, "yAxis": current.data[idx]})
            each.append({"filter": ' '.join(current.filters), "yName": current.Y})

            node_dic[node.id] = each

        #jsonFile = json.dumps(node_dic)
        return node_dic
    
    #def generateJson(self, root, node_dic):
        # Generate JSON representation of tree for Treant to render
        # tree += '"innerHTML"' + ' : ' + '"#chart' + str(root.id) + '"' + ","
        #tree += '"children"' +' : '+'['

        # for i in range(len(root.childrenIndex)):
        #     thisBracket = "{"
        #     thisBracket += '"innerHTML"' + ' : ' + '"#chart' + str(root.childrenIndex[i]) + '"'+","
        #
        #     #if i != len(root.childrenIndex) - 1:
        #         #thisBracket += ","
        #     if node_dic[root.childrenIndex[i]][len(node_dic[root.childrenIndex[i]]) - 1]["childrenIndex"] != []:
        #         arr = node_dic[root.childrenIndex[i]][len(node_dic[root.childrenIndex[i]]) - 1]["childrenIndex"]
        #         thisBracket += '"children"' +' : '+'['
        #         for j in range(len(arr)):
        #             this = "{"
        #             this += '"innerHTML"' + ' : ' + '"#chart' + str(arr[j]) + '"'
        #             if j != len(arr) - 1:
        #                 this += "},"
        #             else:
        #                 this += "}"
        #             thisBracket += this
        #         if i != len(root.childrenIndex)-1:
        #             thisBracket += ']},'
        #         else:
        #             thisBracket += ']}'
        #         tree += thisBracket
        # tree += ']}'

    def drawGraphToPNG(self):
        G = self.graph
        # print(G.numberOfEdges())
        p = nx.drawing.nx_pydot.to_pydot(G)
        p.write_png('graph.png')

    def generateNode(self, root, node_dic):
        # Generate  tree for Treant to render
        node = []
        nodes = list(self.generateNodeDic().values())
        barcharts = []
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
            barcharts.append(bar_chart(yVals, xAttrs, xtitle="", ytitle="", title="", top_right_text="", N=1, width=0.1))

        barcharts_new = []
        for i in barcharts:
            barcharts_new.append(base64.b64encode(i))

        for i in range(len(nodes)):
            node.append(i+1)
            node.append(barcharts_new[i])
        return node

    def generateEdge(self, root, node_dic):
        # Generate  tree for Treant to render
        edgeDic = self.generateNodeDic()
        #print(edgelist)
        edge = []
        for key in edgeDic.keys():
            for i in edgeDic[key][3]['childrenIndex']:
                edge.append(key)
                edge.append(i)
        return edge

    def generateSvg(self, root, node_dic):
        return None

