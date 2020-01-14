package cc.sarisia.aria;

import cc.sarisia.aria.models.AriaException;
import cc.sarisia.aria.models.Entry;
import cc.sarisia.aria.models.Playlist;
import cc.sarisia.aria.models.request.*;
import cc.sarisia.aria.models.response.*;
import cc.sarisia.aria.models.response.Error;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class EndpointController {
    // http://gyamin.hatenablog.com/entry/2017/04/01/225746
    @Autowired
    private DatabaseService db;

    @GetMapping("/")
    public Gaiji getRoot() {
        return new Gaiji();
    }

    @PostMapping("/")
    public Gaiji postGaiji(@RequestBody Gaiji gaiji) {
        return gaiji;
    }

    @SneakyThrows
    @GetMapping("/search")
    public SearchResult postSearch(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "provider", required = false) String provider,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "50", required = false) int limit
    ) {
        return db.search(query, provider, offset, limit);
    }

    // TODO: more RESTful!
    @SneakyThrows
    @PostMapping("/cache")
    public ResponseEntity<Object> postCacheNew(@RequestBody @Valid InsertRequest request) {
        db.insertCache(request);
        return ResponseEntity.ok().body(null);
    }

    // TODO: yes I know this sucks.
    @SneakyThrows
    @PostMapping("/cache/resolve")
    public Entry getCacheResolve(@RequestBody @Valid ResolveRequest request) {
        return db.resolveCache(request);
    }

    @SneakyThrows
    @PostMapping("/gpm/update")
    public ResponseEntity<Object> udpateGPM(@RequestBody @Valid UpdateGPMRequest request) {
        db.updateGPM(request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @PostMapping("/gpm")
    public ExtendedGPMEntry resolveGPM(@RequestBody @Valid ResolveRequest request) {
        System.out.println(request.toString());
        return db.resolveGPM(request);
    }

    @SneakyThrows
    @GetMapping("/gpm")
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
    public ResponseEntity<Object> postPlaylist(@RequestBody @Valid CreatePlaylistRequest request) {
        db.createPlaylist(request);
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
            @RequestBody @Valid AddToPlaylistRequest request
    ) {
        db.addToPlaylist(name, request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @DeleteMapping("/playlist/{name}")
    public ResponseEntity<Object> deleteFromPlaylist(
            @PathVariable(name = "name") String name,
            @RequestBody @Valid DeleteFromPlaylistRequest request
    ) {
        db.deleteFromPlaylist(name, request);
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
    public ResponseEntity<Object> toggleLike(@RequestBody @Valid ToggleLikeRequest request) {
        db.toggleLike(request);
        return ResponseEntity.ok().body(null);
    }

    @SneakyThrows
    @PostMapping("/likes/resolve")
    public Liked resolveLike(@RequestBody @Valid ResolveRequest request) {
        return db.isLiked(request);
    }

    // error handler
    @ExceptionHandler(AriaException.class)
    public ResponseEntity<Error> getException(AriaException e) {
        return new ResponseEntity<>(new Error(e), HttpStatus.BAD_REQUEST);
    }
}
