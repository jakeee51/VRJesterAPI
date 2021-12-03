import re, math


class Vector3d:
   def __init__(self, x: float, y: float, z: float):
      self.x = float(x)
      self.y = float(y)
      self.z = float(z)
      self.point = (float(x), float(y), float(z))
   def __repr__(self):
      val = str(self.point).strip('()')
      return f"<Vector3d([{val}])>"
   def __getitem__(self, idx):
      return self.point[idx]

def getSlope(p1: Vector3d, p2: Vector3d) -> float:
   xyd = abs(p1.x - p2.x)**2 + abs(p1.y - p2.y)**2
   run = math.sqrt(xyd)
   rise = abs(p1.z - p2.z)
   if run == 0:
      ret = 0.0
   else:
      ret = rise / run
   return ret

def getDiff(slope1: float, slope2: float) -> float:
   ret = abs(slope1 - slope2)
   return ret / ((slope1 + slope2) / 2)

def main(data: list, tolerance: float=.25):
   vectors = []; valid_vectors = []
   for point in data:
      point = point.strip('\n').split(',')
      vectors.append(Vector3d(*point))
   # Assume 1st point valid
   valid = vectors[0]; valid_slope = None 
   valid_vectors.append(valid)
   for i in range(1, len(vectors[1:])):
      vector = vectors[i]
      # Get slope
      slope = getSlope(valid, vector)
      if valid_slope == None:
         if slope != 0:
            # Get valid slope
            valid_slope = slope
         print("Within confidence", i)
         valid_vectors.append(vector)
         continue
      # Get % difference between valid slope and slope of current point from the og point
      percent_diff = getDiff(valid_slope, slope)
      # Compare against tolerance ratio
      if percent_diff < tolerance:
         print("Within confidence", i)
         # Append to new dataset of valid points
         valid_vectors.append(vector)
   print(len(valid_vectors), valid_vectors)

if "__main__" == __name__:
   print("Initiated Gesture Recognition Test (Linear):\n")
   with open("rc_fast.csv") as f:
      data = f.readlines()
   print(f"({len(data)} data points)")
   main(data, .35)
