# class Gargoyles:
#     def __init__(self, id, name, age, gargoyle_type, status, hunger, happiness, health, experience, strength, speed, intelligence, last_fed, last_played, left_at, user_id):
#         self.id = id
#         self.name = name
#         self.age = age
#         self.type = gargoyle_type
#         self.status = status
#         self.hunger = hunger
#         self.happiness = happiness
#         self.health = health
#         self.experience = experience
#         self.strength = strength
#         self.speed = speed
#         self.intelligence = intelligence
#         self.last_fed = last_fed
#         self.last_played = last_played
#         self.left_at = left_at
#         self.user_id = user_id

class Gargoyles:
    def __init__(self, id, name, age, hunger, happiness):
        self.id = id
        self.name = name
        self.age = age
        self.hunger = hunger
        self.happiness = happiness

    def __eq__(self, other):
        return self.__dict__ == other.__dict__

    def __repr__(self):
        return f"Gargoyle({self.id}, {self.name}, {self.hunger}, {self.happiness})"
