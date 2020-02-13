package cc.sarisia.aria;

import cc.sarisia.aria.models.Entry;
import cc.sarisia.aria.models.GPMEntry;
import cc.sarisia.aria.models.Playlist;
import cc.sarisia.aria.models.request.BatchRequest;
import cc.sarisia.aria.models.response.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class EndpointController {
    // http://gyamin.hatenablog.com/entry/2017/04/01/225746
    private final DatabaseService db;

    @Autowired
    public EndpointController(DatabaseService db) {
        this.db = db;
    }

    @SneakyThrows
    @PostMapping("/cache")
    public ResponseEntity<Object> postCacheNew(@RequestBody @Valid BatchRequest<Entry> request) {
        db.insertCache(request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @GetMapping("/cache")
    public Entry getCacheResolve(@RequestParam(name = "uri") String uri) {
        return db.resolveCache(uri);
    }

    @SneakyThrows
    @PostMapping("/gpm/update")
    public ResponseEntity<Object> udpateGPM(
            @RequestParam(name = "name") String name,
            @RequestBody @Valid BatchRequest<GPMEntry> request
    ) {
        db.updateGPM(name, request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @GetMapping("/gpm")
    public ExtendedGPMEntry resolveGPM(@RequestParam(name = "uri") String uri) {
        return db.resolveGPM(uri);
    }

    @SneakyThrows
    @GetMapping("/gpm/search")
    public SearchGPMResult searchGPM(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "50", required = false) int limit
    ) {
        return db.searchGPM(query, offset, limit);
    }

    @SneakyThrows
    @GetMapping("/playlist")
    public Playlists getPlaylists() {
        return db.showPlaylists();
    }

    @SneakyThrows
    @PostMapping("/playlist")
    public ResponseEntity<Object> postPlaylist(@RequestParam(name = "name") String name) {
        db.createPlaylist(name);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @DeleteMapping("/playlist")
    public ResponseEntity<Object> deletePlaylist(@RequestParam(name = "name") String name) {
        db.deletePlaylist(name);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @GetMapping("/playlist/{name}")
    public Playlist getPlaylistEntity(
            @PathVariable("name") String name,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "limit", defaultValue = "50", required = false) int limit,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset
    ) {
        return db.playlist(name, query, limit, offset);
    }

    @SneakyThrows
    @PostMapping("/playlist/{name}")
    public ResponseEntity<Object> addToPlaylistEntry(
            @PathVariable("name") String name,
            @RequestBody @Valid BatchRequest<String> request
    ) {
        db.addToPlaylist(name, request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @DeleteMapping("/playlist/{name}")
    public ResponseEntity<Object> deleteFromPlaylist(
            @PathVariable(name = "name") String name,
            @RequestParam(name = "uri") String uri
    ) {
        db.deleteFromPlaylist(name, uri);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @GetMapping("/likes")
    public Playlist likes(
            @RequestParam(name = "limit", defaultValue = "50", required = false) int limit,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset
    ) {
        return db.showLikes(limit, offset);
    }

    @SneakyThrows
    @PostMapping("/likes")
    public ResponseEntity<Object> toggleLike(
            @RequestParam(name = "uri") String uri,
            @RequestParam(name = "like") boolean like
    ) {
        db.toggleLike(uri, like);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @GetMapping("/likes/resolve")
    public Liked resolveLike(@RequestParam(name = "uri") String uri) {
        return db.isLiked(uri);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> onDataAccessException(DataAccessException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
