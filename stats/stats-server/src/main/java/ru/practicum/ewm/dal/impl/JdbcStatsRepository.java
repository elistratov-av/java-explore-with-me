package ru.practicum.ewm.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.dal.StatsRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcStatsRepository implements StatsRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String INSERT_HIT_QUERY = "INSERT INTO endpointhits (app, uri, ip, timestamp)\n" +
            "VALUES(:app, :uri, :ip, :timestamp)";

    private static String getStatsQuery(boolean checkUri, boolean unique) {
        return "SELECT app, uri, " + (unique ? "COUNT(DISTINCT ip)" : "COUNT(*)") + " hits\n" +
                "FROM endpointhits\n" +
                "WHERE (timestamp >= :start AND timestamp <= :end)" + (checkUri ? " AND (uri IN (:uris))\n" : "\n") +
                "GROUP BY app, uri\n" +
                "ORDER BY hits DESC";
    }

    // endregion

    // region Mapper

    private static ViewStatsDto mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        ViewStatsDto stats = new ViewStatsDto();
        stats.setApp(rs.getString("app"));
        stats.setUri(rs.getString("uri"));
        stats.setHits(rs.getLong("hits"));
        return stats;
    }

    // endregion

    @Override
    public Long hit(EndpointHitDto endpointHit) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_HIT_QUERY,
                new MapSqlParameterSource("app", endpointHit.getApp())
                        .addValue("uri", endpointHit.getUri())
                        .addValue("ip", endpointHit.getIp())
                        .addValue("timestamp", endpointHit.getTimestamp()),
                gkh, new String[]{"id"});
        return gkh.getKeyAs(Long.class);
    }

    @Override
    public List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        String sql = getStatsQuery(uris != null && uris.length > 0, unique);
        return jdbc.query(sql,
                new MapSqlParameterSource("start", start)
                        .addValue("end", end)
                        .addValue("uris", uris != null ? List.of(uris) : null),
                JdbcStatsRepository::mapRowTo);
    }
}
