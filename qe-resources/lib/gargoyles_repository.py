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


connection = DatabaseConnection()
connection.connect()
gargoylesrepository = GargoylesRepository(connection)


gargoyle_id = gargoylesrepository.create_gargoyle('TestGargoyle2', user_id=1)
print(f"Created gargoyle with ID: {gargoyle_id}")

