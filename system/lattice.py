from node import Node
class Lattice:
	'''
	Lattice is a DAG (graph) of Node objects
	'''
	def __init__(self):
		# contain a graph of Node objects 
		self.graph = populateDummyLattice()
		self.type = 'summarized' #'full' or summarized
	def generateDashboard(self):
		raise NotImplementedError
	def populateDummyLattice():
		#populate a lattice of given visualizations to test rendering functions
		raise NotImplementedError
	def __repr__(self):
		# print formatted string summarization of object when 'print latticeObj' is called
		# return number of nodes in lattice and other attributes (would be nice to display graph structure)
		raise NotImplementedError
