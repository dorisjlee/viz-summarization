


import networkx as nx


# 	Lattice is a DAG (graph) of Node objects



class Lattice:

    # contain a graph of Node objects
    def __init__(self):
        self.graph = nx.DiGraph()
        self.type = 'summarized' #'full' or summarized
        self.nodeDic = {}
        #

    # not worry about
    def generateDashboard(self):
        raise NotImplementedError

    # testing
    # populate a lattice of given visualizations to test rendering functions
    def populateDummyLattice(self):
        raise NotImplementedError

    # print formatted string summarization of object when 'print latticeObj' is called
    # return number of nodes in lattice and other attributes (would be nice to display graph structure
    # TODO
    def __repr__(self):
        return '(Nodes: %s, numberOfNodes: %s)' % (self.getNodes(), self.numberOfNodes())


        # wrapper function for adding a node
    def addNode(self, node):
        node.id = self.numberOfNodes() + 1
        self.graph.add_node(node)

    # wrapper function for adding multiple nodes
    def addMultiNodes(self, list):
        for node in list:
            self.addNode(node)

    # wrapper function for adding a node with (k,v) attribute
    # [(race,black), (age,15), (gender,female)]
    def addNodePlus(self, node_name, attribute):
        for (k,v) in attribute:
            self.graph.add_node(node_name,k=v)

    # wrapper function for deleting a node
    def deleteNode(self, node_name):
        self.graph.remove_node(node_name)

    # wrapper function for adding an edge
    def addEdge(self, head, tail):
            self.graph.add_edge(head, tail)

    # wrapper function for adding an Edge with (k,v) attribute
    # [(race,black), (age,15), (gender,female)]
    def addEdgePlus(self, head, tail, attribute):
            for (k, v) in attribute:
                self.graph.add_edge(head, tail, k=v)


    # wrapper function for deleting an Edge
    def deleteEdge(self, head, tail):
        self.graph.remove_edge(head, tail)

    # getter
    def getNodes(self):
        return self.graph.nodes()

    # setter
    def getEdges(self):
        return self.graph.edges()

    # number of nodes
    def numberOfNodes(self):
        return self.graph.number_of_nodes()

    # number of edges
    def numberOfEdges(self):
        return self.graph.number_of_edges()