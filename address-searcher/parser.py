import csv
import xml.etree.ElementTree
from address import Address
class FileParsing:

    def __init__(self, file_path):
        self.file_path = file_path
        self.addresses = []
    def parse(self):
        #Определение способа парсинга
        if self.file_path.endswith('.csv'):
            return self.parse_csv()
        elif self.file_path.endswith('.xml'):
            return self.parse_xml()
        else:
            raise ValueError("Обрабатываются только файлы CSV или XML.")
    def parse_csv(self):
        #Парсинг CSV файла
        try:
            with open(self.file_path, 'r', encoding='utf-8') as csvfile:
                reader = csv.DictReader(csvfile, delimiter=';')
                for row in reader:
                    self.addresses.append(Address(row["city"], row["street"], row["house"], row["floor"]))
        except Exception as e:
            print(f"Ошибка при чтении CSV: {e}")
        return self.addresses
    def parse_xml(self):
        #Парсинг XML файла
        try:
            tree = xml.etree.ElementTree.parse(self.file_path)
            root = tree.getroot()
            for item in root.findall("item"):
                self.addresses.append(Address(item.get("city"), item.get("street"), item.get("house"), item.get("floor")))
        except Exception as e:
            print(f"Ошибка при чтении XML: {e}")
        return self.addresses