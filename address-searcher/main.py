import time
from parser import FileParser
from stats import Statistics

class Searcher:
    def run(self):
        while True:
            input_name = input("\nВведите путь до файла (или \"exit\" для выхода): ").strip()
            if input_name.lower() == "exit":
                print("Завершение работы.")
                break
            try:
                start_time = time.time()
                parser = FileParser(input_name)
                addresses = parser.parse()
                if not addresses:
                    print("Файл пустой или с ошибками.")
                    continue
                statistics = Statistics(addresses)
                duplicates = statistics.find_duplicates()
                floor_stats = statistics.floor_statistics()
                statistics.print_statistics(duplicates, floor_stats)
                elapsed_time = time.time() - start_time
                print(f"\nВремя обработки {input_name}: {elapsed_time:.2f} секунд")
            except Exception as e:
                print(f"Ошибка обработки {input_name}: {e}")
if __name__ == "__main__":
    app = Searcher()
    app.run()