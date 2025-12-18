from database_connection import DatabaseConnection

class GargoylesRepository:
    def __init__(self, connection):
        self._connection = connection
    
    def create_gargoyle(self, name, user_id, age=0, gargoyle_type='CHILD', status='ACTIVE', 
        hunger=0, happiness=0, health=100, experience=0, 
        strength=10, speed=10, intelligence=10, 
        last_fed=None, last_played=None, left_at=None):

        query = '''
            INSERT INTO gargoyles 
            (name, age, type, status, hunger, happiness, health, experience, strength, speed, intelligence, last_fed, last_played, left_at, user_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            RETURNING id
            '''
        
        result = self._connection.execute(query, [
                name, age, gargoyle_type, status, hunger, happiness, health, experience,
                strength, speed, intelligence, last_fed, last_played, left_at, user_id
        ])
        
        return result[0]['id'] if result else None

    def delete_gargoyle(self, gargoyle_id):
        query = 'DELETE FROM gargoyles WHERE id = %s'
        result = self._connection.execute(query, [gargoyle_id])
        return result 

    def get_gargoyle_by_id(self, gargoyle_id):
        query = 'SELECT * FROM gargoyles WHERE id = %s'
        result = self._connection.execute(query, [gargoyle_id])
        return result[0] if result else None

    def update_hunger(self, gargoyle_id, new_hunger):
        query = 'UPDATE gargoyles SET hunger = %s WHERE id = %s'
        result = self._connection.execute(query, [new_hunger, gargoyle_id])
        return result[0] if result else None       



connection = DatabaseConnection()
connection.connect()
gargoylesrepository = GargoylesRepository(connection)


#Create Gargoyle 
# gargoyle_id = gargoylesrepository.create_gargoyle('TestGargoyle2', user_id=1)
# print(f"Created gargoyle with ID: {gargoyle_id}")

# Delete Gsrgoyle 
# gargoylesrepository.delete_gargoyle(8)

# Select Gargoyle by ID
# gargoyle = gargoylesrepository.get_gargoyle_by_id(7)
# gg_id = gargoyle['id']
# gg_name = gargoyle['name']
# print(f"Gargoyle ID: {gg_id} & Gargoyle name: {gg_name}")



updated_gargoyle = gargoylesrepository.update_hunger(1, 80)



