import re, os

def clean(file_name):
   with open(file_name) as f:
##      print(f.readline(), end ='')
      lines = f.readlines()
      length = len(lines)
      print(f"({length} total records)")
      sub = tuple()
      offset = []; first = True
      for line in lines:
         line = line.strip('\n')
         vector = line.split(',')
         if first:
            sub = (float(vector[0]), float(vector[1]), float(vector[2]))
            first = False
         for i in range(3):
            vector[i] = float(vector[i]) - sub[i]
         offset.append(str(vector).strip("[]").replace(' ', ''))

   with open(file_name, 'w') as w:
      w.write("x,y,z\n")
      for record in offset:
         w.write(f"{record}\n")

for file in os.listdir():
   if re.search(r".csv$", file):
      print(file)
      clean(file)
