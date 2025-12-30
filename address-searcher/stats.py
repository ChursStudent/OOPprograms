from collections import defaultdict
class Statistics:
    def __init__(self, addresses):
        self.addresses = addresses
    def find_duplicates(self):
        #Поиск количества дубликатов
        counter = defaultdict(int)
        for address in self.addresses:
            counter[address.as_key()] += 1
        duplicates = {key: count for key, count in counter.items() if count > 1}
        return duplicates
    def floor_statistics(self):
        #Статистика по этажам
        stats = defaultdict(lambda: defaultdict(int))
        for address in self.addresses:
            stats[address.city][address.floor] += 1
        return stats
    def print_statistics(self, duplicates, floor_stats):
        #Вывод дубликатов и этажей
        print("\nДублирующиеся записи:")
        for (city, street, house, floor), count in duplicates.items():
            print(f"{city}, {street}, дом {house}, {floor} этажа - {count} раз")
        print("\nСтатистика этажей в городах:")
        for city, floors in floor_stats.items():
            print(f"\n{city}:")
            for floor, count in sorted(floors.items(), key=lambda x: x[0]):
                print(f"{floor} этажных: {count}")