import json
import re

class data_process(object):
    def __init__(self):
        self.data_path = "taxi-trajectory/train.csv"
        
    def calculate(self):
        f = open(self.data_path)
        line = f.readline()
        num = 0
        while line:
            line = line.replace("\"","")
            part_list = line.split(",", 8)
            if part_list[7] != "True":
                trajectory = part_list[8][2: -1]
                number_list = re.findall("\[.*?\]", trajectory)
                num += len(number_list)
            line = f.readline()
            print("total number: ", num)
        f.close()

    def make_all(self, output_path):
        f = open(self.data_path)
        line = f.readline()
        line = f.readline()  # 多一行是为了把开头去掉。。。
        line_max = 1000000
        file_count = 0
        current_file_line = 0
        plainf = open(output_path+str(file_count)+".txt", 'w')
        while line:
            # print(file_count, current_file_line)
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
                    # print(out_line, end="")
                    plainf.write(out_line)
                    current_file_line += 1
                    if line_max == current_file_line:
                        current_file_line = 0
                        plainf.close()
                        file_count += 1
                        print(file_count)
                        plainf = open(output_path+str(file_count)+".txt", 'w')
                # print("id: ", taxi_id, "; time: ", timestamp, "; trajectory: ", number_list)
                # print("id: ", taxi_id, "; time: ", timestamp, "; trajectory: ", trajectory)
            line = f.readline()
        f.close()
        plainf.close()

    def make_txt(self, output_path, number):
        f = open(self.data_path)
        # jf = open("data/json/100000.json", "w")
        # jf.close()
        plainf = open(output_path, 'w')
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
    # pp.make_txt("data/300.txt", 300)
    pp.make_all("data/externalData/part")
    # pp.calculate()
    # pp.print_data(5)
