# Look at superset/viz.py to see how visualizations classes in superset should be written.
class Viz:
	'''
	A vizObj contains the data values for generating the visualization.
	'''
	def __init__(self):
		self.X = []
		self.Y = []
		self.filters = [] # List of constraints
		self.agg_func = 'SUM' # aggregation function 
		self.data = [] #list of data values y1,y2...
		self.expectation = get_expectation()

	def get_expectation(self,type):
		# models for generating user expectation
		raise NotImplementedError
	def __repr__(self):
		raise NotImplementedError
