import json
import re

class data_process(object):
    def __init__(self):
        self.data_path = "taxi-trajectory/train.csv"
        
    def make_txt(self, output_path, number):
        f = open(self.data_path)
        # jf = open("data/json/100000.json", "w")
        # jf.close()
        plainf = open(output_path)
        line = f.readline()
        line = f.readline()
        countMax = number
        count = 0
        while count < countMax:
            line = line.replace("\"","")
            part_list = line.split(",", 8)
            if part_list[7] != "True":
                timestamp = int(part_list[5])
                trajectory = part_list[8][2: -1]
                number_list = re.findall("\[.*?\]", trajectory)
                value = part_list[4]+","+part_list[1]
                for coord in number_list:
                    timestamp = timestamp + 30
                    out_line = str(timestamp) + "|" + coord[1:-1] + "|" + value+"\n"
                    print(out_line, end="")
                    plainf.write(out_line)
                    count = count+1
                # print("id: ", taxi_id, "; time: ", timestamp, "; trajectory: ", number_list)
                # print("id: ", taxi_id, "; time: ", timestamp, "; trajectory: ", trajectory)
            line = f.readline()
        f.close()
        plainf.close()

    def print_data(self, line_num):
        f = open(self.data_path)
        line = f.readline()
        count = 0
        while count < line_num:
            print(line, end="")
            line = f.readline()
            count = count+1

if __name__=="__main__":
    pp = data_process()
    # pp.make_txt("data/100000.txt", 200)
    pp.print_data(5)
