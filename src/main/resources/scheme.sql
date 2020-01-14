-- table playlist: contains Playlist
CREATE TABLE IF NOT EXISTS playlist (
    id serial PRIMARY KEY, -- https://www.postgresql.org/docs/10/datatype-numeric.html#DATATYPE-SERIAL
    name text UNIQUE COLLATE "ja-JP-x-icu",
    ownerId int NOT NULL,
    groupId int
);

-- table playlist_entry: contains Song in Playlist
CREATE TABLE IF NOT EXISTS playlist_entry (
    playlistId int,
    uri text,
    isThumbnail bool NOT NULL,
    PRIMARY KEY (playlistId, uri)
);

-- table user: contains User
CREATE TABLE IF NOT EXISTS aria_user (
    id serial PRIMARY KEY,
    name text NOT NULL
);

-- table group: contains Group
CREATE TABLE IF NOT EXISTS aria_group (
    id serial PRIMARY KEY,
    name text NOT NULL,
    ownerId int NOT NULL,
    canRead bool NOT NULL,
    canEdit bool NOT NULL
);

-- table group_member
CREATE TABLE IF NOT EXISTS group_member (
    groupId int,
    userId int,
    PRIMARY KEY (groupId, userId)
);

-- table song
CREATE TABLE IF NOT EXISTS entry (
    uri text PRIMARY KEY,
    provider text NOT NULL,
    title text NOT NULL COLLATE "ja-JP-x-icu",
    thumbnail text NOT NULL,
    liked bool NOT NULL,
    meta text
);

CREATE INDEX IF NOT EXISTS index_entry_title ON entry (title);

-- table gpm_meta
CREATE TABLE IF NOT EXISTS gpm_meta (
    uri text PRIMARY KEY,
    gpmUser text NOT NULL,
    id text NOT NULL,
    title text NOT NULL COLLATE "ja-JP-x-icu",
    artist text COLLATE "ja-JP-x-icu",
    album text COLLATE "ja-JP-x-icu",
    thumbnail text,
    thumbnailSmall text
);

CREATE INDEX IF NOT EXISTS index_gpm_meta_title ON gpm_meta (title);
CREATE INDEX IF NOT EXISTS index_gpm_meta_artist ON gpm_meta (artist);
CREATE INDEX IF NOT EXISTS index_gpm_meta_album ON gpm_meta(album);
