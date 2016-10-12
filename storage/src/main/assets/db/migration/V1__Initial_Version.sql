CREATE TABLE post (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id TEXT NOT NULL,
    post_title TEXT NOT NULL DEFAULT "",
    post_published INTEGER default 0,
    post_label TEXT,
    post_image TEXT,
    post_track TEXT,
    -- 0 not favorite
    -- 1 favorite
    post_favorite INTEGER default 0,
    post_track_url TEXT default "http://mixes.bassblog.pro/DNBTV_Live_219_5_Years_of_Titan_Records_-_Glass_Cobra.mp3",
    UNIQUE (post_id) ON CONFLICT IGNORE
);