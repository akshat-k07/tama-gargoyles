from lib.gargoyles import *

class GargoylesRepository:
    def __init__(self, connection):
        self._connection = connection
    def all(self):
        rows = self._connection.execute('SELECT * from albums')
        albums = []
        for row in rows:
            item = Album(row["id"], row["title"], row["release_year"], row["artist_id"])
            albums.append(item)
        return albums
    def find(self, album_id):
        rows = self._connection.execute(
            'SELECT * from albums WHERE id = %s', [album_id])
        if rows != []:
            row = rows[0]
            return Album(row["id"], row["title"], row["release_year"], row["artist_id"])
        else:
            return None

    def create(self, album):
        result = self._connection.execute('INSERT INTO albums (title, release_year, artist_id) VALUES (%s, %s, %s)', [
        album.title, album.release_year, album.artist_id])
        return result

    def delete(self, album_id):
        result = self._connection.execute(
            'DELETE FROM albums WHERE id = %s', [album_id])
        return result
