package cc.sarisia.aria;

import cc.sarisia.aria.models.Entry;
import cc.sarisia.aria.models.GPMEntry;
import cc.sarisia.aria.models.Playlist;
import cc.sarisia.aria.models.request.*;
import cc.sarisia.aria.models.response.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Repository
public class DatabaseService {
    private final NamedParameterJdbcTemplate db;

    @Autowired
    public DatabaseService(NamedParameterJdbcTemplate db) {
        this.db = db;
    }

    @SneakyThrows
    public SearchResult search(String query, String provider, int offset, int limit) {
        var QUERY = "SELECT * FROM entry WHERE title LIKE :query ORDER BY title ASC LIMIT :limit OFFSET :offset";
        var COUNT_QUERY = "SELECT COUNT(title) FROM entry WHERE title LIKE :query";
        if (provider != null) {
            // java dont't have multiline string
            // fuck java use kotlin
            QUERY = "SELECT * FROM entry " +
                    "WHERE provider = :provider AND title LIKE :query ORDER BY title ASC " +
                    "LIMIT :limit OFFSET :offset";
            COUNT_QUERY = "SELECT COUNT(title) FROM entry WHERE provider = :provider AND title LIKE :query";
        }
        var params = new MapSqlParameterSource()
                .addValue("query", toSQLQueryString(query))
                .addValue("provider", provider)
                .addValue("limit", limit)
                .addValue("offset", offset);
        var count = db.queryForObject(COUNT_QUERY, params, int.class);
        var rows = db.query(
                QUERY,
                params,
                (rs, i) -> {
                    var entry = new Entry();
                    entry.setUri(rs.getString(1));
                    entry.setProvider(rs.getString(2));
                    entry.setTitle(rs.getString(3));
                    entry.setThumbnail(rs.getString(4));
                    entry.setLiked(rs.getBoolean(5));
                    entry.setMeta(rs.getString(6));
                    return entry;
                });
        var result = new SearchResult();
        result.setHit(count);
        result.setResults(rows);
        return result;
    }

    @SneakyThrows
    public void insertCache(InsertRequest request) {
        var QUERY = "INSERT INTO entry VALUES (:uri, :provider, :title, :thumbnail, :liked, :meta)";
        // https://stackoverflow.com/questions/28319064/java-8-best-way-to-transform-a-list-map-or-foreach
        var paramsArray = request.getEntries().stream()
                .map(BeanPropertySqlParameterSource::new)
                // https://stackoverflow.com/questions/29447561/how-do-java-8-array-constructor-references-work
                .toArray(BeanPropertySqlParameterSource[]::new);
        db.batchUpdate(QUERY, paramsArray);
    }

    @SneakyThrows
    public Entry resolveCache(String uri) {
        var QUERY = "SELECT * FROM entry WHERE uri = ?";
        return db.getJdbcTemplate().queryForObject(
                QUERY,
                new Object[]{uri},
                (rs, i) -> {
                    var entry = new Entry();
                    entry.setUri(rs.getString(1));
                    entry.setProvider(rs.getString(2));
                    entry.setTitle(rs.getString(3));
                    entry.setThumbnail(rs.getString(4));
                    entry.setLiked(rs.getBoolean(5));
                    entry.setMeta(rs.getString(6));
                    return entry;
                }
        );
    }

    @SneakyThrows
    @Transactional
    public void updateGPM(UpdateGPMRequest request) {
        var params = request.getEntries().stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(BeanPropertySqlParameterSource[]::new);
        var GPM_QUERY = "INSERT INTO gpm_meta " +
                "VALUES (:uri, :gpmUser, :id, :title, :artist, :album, :thumbnail, :thumbnailSmall)";
//        var QUERY = "INSERT INTO entry " +
//                "VALUES (:uri, 'gpm', :fullTitle, :thumbnail, false, NULL)";
        db.batchUpdate(GPM_QUERY, params);
//        db.batchUpdate(QUERY, params);
    }

    @SneakyThrows
    public ExtendedGPMEntry resolveGPM(String uri) {
        var QUERY = "SELECT * FROM gpm_meta WHERE id = ?";
        var gpm = db.getJdbcTemplate().queryForObject(
                QUERY,
                new Object[]{uri},
                (rs, i) -> {
                    var g = new GPMEntry();
                    g.setUri(rs.getString(1));
                    g.setGpmUser(rs.getString(2));
                    g.setId(rs.getString(3));
                    g.setTitle(rs.getString(4));
                    g.setArtist(rs.getString(5));
                    g.setAlbum(rs.getString(6));
                    g.setThumbnail(rs.getString(7));
                    return g;
                }
        );
        var ret = new ExtendedGPMEntry();
        ret.setUri(gpm.getUri());
        ret.setProvider("gpm");
        ret.setTitle(gpm.toFullTitle());
        ret.setThumbnail(gpm.getThumbnail());
        ret.setMeta(gpm);
        var INSERT_QUERY = "INSERT INTO entry " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING";
        db.getJdbcTemplate().update(
                INSERT_QUERY,
                ret.getUri(),
                ret.getProvider(),
                ret.getTitle(),
                ret.getThumbnail(),
                false,
                null);
        return ret;
    }

    @SneakyThrows
    public SearchGPMResult searchGPM(String query, int offset, int limit) {
        var QUERY = "SELECT * FROM gpm_meta " +
                "WHERE gpmUser || title || artist || album like ? " +
                "LIMIT ? OFFSET ?";
        var results = db.getJdbcTemplate().query(
                QUERY,
                new Object[]{toSQLQueryString(query), limit, offset},
                (rs, i) -> {
                    var g = new GPMEntry();
                    g.setUri(rs.getString(1));
                    g.setGpmUser(rs.getString(2));
                    g.setId(rs.getString(3));
                    g.setTitle(rs.getString(4));
                    g.setArtist(rs.getString(5));
                    g.setAlbum(rs.getString(6));
                    g.setThumbnail(rs.getString(7));
                    return g;
                }
        );

//        var entries = results.stream()
//                .map(e -> e.toEntry())
//                .collect(Collectors.toList());
        var ret = new SearchGPMResult();
        ret.setResults(results);
        return ret;
    }

    @SneakyThrows
    public Playlists showPlaylists() {
        var QUERY = "SELECT * FROM playlist ORDER BY name ASC";
        var playlists = db.query(
                QUERY,
                (rs, i) -> {
                    var pl = new Playlist();
                    pl.setId(rs.getInt(1));
                    pl.setName(rs.getString(2));
                    pl.setOwnerId(rs.getInt(3));
                    pl.setGroupId(rs.getInt(4));
                    return pl;
                }
        );

        var THUMBNAIL_QUERY = "SELECT thumbnail FROM playlist_entry JOIN entry USING (uri) " +
                "WHERE playlistId = ? AND isThumbnail = true AND thumbnail != ''";
        var COUNT_QUERY = "SELECT count(*) FROM playlist_entry WHERE playlistId = ?";
        // TODO: concurrent
        for (var pl: playlists) {
            var thumbnails = db.getJdbcTemplate().queryForList(
                    THUMBNAIL_QUERY,
                    new Object[]{pl.getId()},
                    String.class
            );
            var count = db.getJdbcTemplate().queryForObject(COUNT_QUERY, new Object[]{pl.getId()}, int.class);
            pl.setThumbnails(thumbnails);
            pl.setLength(count);
        }

        var pls = new Playlists();
        pls.setPlaylists(playlists);
        return pls;
    }

    @SneakyThrows
    public void createPlaylist(String name) {
        var QUERY = "INSERT INTO playlist VALUES (DEFAULT, ?, ?, ?)";
        db.getJdbcTemplate().update(
                QUERY, name, 0, 0);
    }

    @SneakyThrows
    public Playlist playlist(String name, String query, int limit, int offset) {
        var PLAYLIST_QUERY = "SELECT * from playlist where name = ?";
        var playlist = db.getJdbcTemplate().queryForObject(
                PLAYLIST_QUERY,
                new Object[]{name},
                (rs, i) -> {
                    var pl = new Playlist();
                    pl.setId(rs.getInt(1));
                    pl.setName(rs.getString(2));
                    pl.setOwnerId(rs.getInt(3));
                    pl.setGroupId(rs.getInt(4));
                    return pl;
                }
        );

        var params = new MapSqlParameterSource()
                .addValue("plid", playlist.getId())
                .addValue("limit", limit)
                .addValue("offset", offset);
        var ENTRY_QUERY = "SELECT * FROM playlist_entry LEFT JOIN entry USING (uri) " +
                "WHERE playlistId = :plid ORDER BY title ASC LIMIT :limit OFFSET :offset";
        if (query != null) {
            ENTRY_QUERY = "SELECT * FROM playlist_entry LEFT JOIN entry USING (uri) " +
                    "WHERE playlistId = :plid AND title LIKE :query ORDER BY title ASC LIMIT :limit OFFSET :offset";
            params.addValue("query", toSQLQueryString(query));
        }
        var thumbnails = new ArrayList<String>();
        var unknowns = new ArrayList<String>();
        var entries = db.query(
                ENTRY_QUERY,
                params,
                (rs, i) -> {
                    var entry = new Entry();
                    entry.setUri(rs.getString(1));
                    entry.setProvider(rs.getString(4));
                    if (entry.getProvider() == null) {
                        unknowns.add(entry.getUri());
                        return entry;
                    }
                    entry.setTitle(rs.getString(5));
                    entry.setThumbnail(rs.getString(6));
                    if (rs.getBoolean(3)) {
                        thumbnails.add(entry.getThumbnail());
                    }
                    entry.setLiked(rs.getBoolean(7));
                    entry.setMeta(rs.getString(8));
                    return entry;
                }
        );
        playlist.setThumbnails(thumbnails);
        playlist.setUnknownURIs(unknowns);
        playlist.setEntries(entries.stream().filter(e -> e.getProvider() != null).collect(Collectors.toList()));
        return playlist;
    }

    @SneakyThrows
    public void addToPlaylist(String name, AddToPlaylistRequest request) {
        // TODO: check query plans, determine whether to use sub query or not
        var PLID_QUERY = "SELECT id FROM playlist where name = ?";
        var plID = db.getJdbcTemplate().queryForObject(
                PLID_QUERY,
                new Object[]{name},
                int.class
        );

        var QUERY = "INSERT INTO playlist_entry VALUES (:id, :uri, :thumbnail)";
        var paramsArray = request.getUris().stream()
                .map(u -> new AddToPlaylistSQLParams(plID, u, true))
                .map(BeanPropertySqlParameterSource::new)
                .toArray(BeanPropertySqlParameterSource[]::new);
        db.batchUpdate(QUERY, paramsArray);
    }

    @SneakyThrows
    public void deleteFromPlaylist(String name, String uri) {
        var QUERY = "DELETE FROM playlist_entry " +
                "WHERE uri = ? AND playlistId = (SELECT id FROM playlist WHERE name = ?)";
        db.getJdbcTemplate().update(QUERY, new Object[]{uri, name});
    }

    @SneakyThrows
    @Transactional
    public void deletePlaylist(String name) {
        var ENTRY_QUERY = "DELETE FROM playlist_entry " +
                "WHERE playlistId = (SELECT id FROM playlist WHERE name = ?)";
        var QUERY = "DELETE FROM playlist WHERE name = ?";
        db.getJdbcTemplate().update(ENTRY_QUERY, name);
        db.getJdbcTemplate().update(QUERY, name);
    }

    @SneakyThrows
    public Playlist showLikes(int limit, int offset) {
        var QUERY = "SELECT * FROM entry " +
                "WHERE liked = true ORDER BY title ASC LIMIT ? OFFSET ?";
        var thn = new ArrayList<String>();
        var entries = db.getJdbcTemplate().query(
            QUERY,
            new Object[]{limit, offset},
            (rs, i) -> {
                var entry = new Entry();
                entry.setUri(rs.getString(1));
                entry.setProvider(rs.getString(2));
                entry.setTitle(rs.getString(3));
                entry.setThumbnail(rs.getString(4));
                if (entry.getThumbnail() != "") {
                    thn.add(entry.getThumbnail());
                }
                entry.setLiked(rs.getBoolean(5));
                entry.setMeta(rs.getString(6));
                return entry;
            }
        );

        var pl = new Playlist();
        pl.setId(-1);
        pl.setName("Likes");
        pl.setOwnerId(0);
        pl.setGroupId(0);
        pl.setEntries(entries);
        pl.setThumbnails(thn);
        try {
            pl.setThumbnails(thn.subList(0, 10));
        } catch (Exception e) {}
        return pl;
    }

    @SneakyThrows
    public void toggleLike(String uri, boolean like) {
        var QUERY = "UPDATE entry SET liked = ? WHERE uri = ?";
        db.getJdbcTemplate().update(QUERY, new Object[]{like, uri});
    }

    @SneakyThrows
    public Liked isLiked(String uri) {
        var QUERY = "select liked from entry where uri = ?";
        var isLiked = db.getJdbcTemplate().queryForObject(
                QUERY,
                new Object[]{uri},
                boolean.class
        );
        var ret = new Liked();
        ret.setLiked(isLiked);
        return ret;
    }

    public static String toSQLQueryString(String raw) {
        return "%" + raw.replace("%", "\\%")
                .replace("_", "\\_")
                .replace(" ", "%") + "%";
    }
}
