# Look at superset/viz.py to see how visualizations classes in superset should be written.

import networkx as nx


class vizObj:
    '''
    A vizObj contains the data values for generating the visualization.

    '''
    def __init__(self, XLists = [], yaxis = ""):
        self.X = XLists #strings
        self.Y = yaxis
        self.filters = [] # List of constraints, it is the same as the filters in parents. [("gender","female")]
        self.agg_func = 'SUM' # vaggregation function
        self.data = [] #list of data values y1,y2...
        self.expectation = self.get_expectation('NULL')

    def get_expectation(self,type):
        # models for generating user expectation
        return 0

    # TODO
    def __repr__(self):
        return '(X_axis: %s, Y_axis: %s, filters: %s, data: %s)' % (self.X, self.Y, self.filters, self.data)

    def setX(self, strings):
        self.X.append(strings)

    def setY(self, yaxis):
        self.Y = yaxis

    def setFilters(self, constraints):
        self.filters = constraints

    def setAgg(self, agg):
        self.agg_func = agg

    def setData(self, list):
        self.data = list



# class Constraint