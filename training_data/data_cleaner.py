import re

with open("VRJester_Data_idle_2.csv") as f:
   print(f.readline(), end ='')
   lines = f.readlines()
   length = len(lines)
   print(f"({length} total records)")
   sub = (239, 69, 208)
   offset = []
   for line in lines:
      line = line.strip('\n')
      line = re.sub(r"(Device: pos:\()|\) dir: \(.+", '', line)
      vector = line.split(',')
      for i in range(3):
         vector[i] = float(vector[i]) - sub[i]
      offset.append(str(vector).strip("[]").replace(' ', ''))

with open("offset_idle_2.csv", 'w') as w:
   w.write("x,y,z\n")
   for record in offset:
      w.write(f"{record}\n")
