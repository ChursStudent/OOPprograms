class Address:
    def __init__(self, city, street, house, floor):
        self.city = city
        self.street = street
        self.house = int(house)
        self.floor = int(floor)
    def object(self):
        return (self.city, self.street, self.house, self.floor)