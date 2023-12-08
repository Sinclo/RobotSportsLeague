package robotSportsLeague.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import robotSportsLeague.web.model.RobotTeam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcRobotTeamRepository implements RobotTeamRepository {

    private JdbcTemplate jdbc;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public JdbcRobotTeamRepository (JdbcTemplate jdbc){
        this.jdbc = jdbc;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbc).withTableName("SportsTeam").usingGeneratedKeyColumns("id");
    }

    @Override
    public List<RobotTeam> findAll() {
        return jdbc.query("SELECT id, teamname, ownerfirstname, ownerlastname, createDate, lastUpdatedDate " +
                                "FROM SportsTeam", this::mapRowToTable);
    }

    @Override
    public RobotTeam findOne(Integer id) {
        return jdbc.queryForObject("SELECT id, teamname, ownerfirstname, ownerlastname, createDate, lastUpdatedDate " +
                                "FROM SportsTeam " +
                                "WHERE id = ?", this::mapRowToTable, id);
    }

    @Override
    public RobotTeam save(RobotTeam robotTeam){
        jdbc.update("INSERT INTO SportsTeam (teamname, ownerfirstname, ownerlastname) " +
                        "VALUES (?, ?, ?)",
                    robotTeam.getTeamName(),
                    robotTeam.getOwnerFirstName(),
                    robotTeam.getOwnerLastName());
        return robotTeam;
    }

    public RobotTeam update(RobotTeam robotTeam){
        jdbc.update("UPDATE SportsTeam SET ownerfirstname = ?, ownerlastname = ?, lastUpdatedDate = ? " +
                        "WHERE teamname = ?",
                robotTeam.getOwnerFirstName(),
                robotTeam.getOwnerLastName(),
                robotTeam.getLastUpdatedDate(),
                robotTeam.getTeamName());
        return robotTeam;
    }

    @Override
    public Iterable<RobotTeam> save(Iterable<? extends RobotTeam> iterable) {
        return null;
    }

    @Override
    public boolean exists(Integer integer) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public void delete(RobotTeam robotTeam) {

    }

    @Override
    public void delete(Iterable<? extends RobotTeam> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public RobotTeam findOne(String teamName) {
        return jdbc.queryForObject("SELECT id, teamname, ownerfirstname, ownerlastname, createDate, lastUpdatedDate " +
                "FROM SportsTeam " +
                "WHERE teamname = ?", this::mapRowToTable, teamName);
    }

    @Override
    public RobotTeam mapRowToTable(ResultSet rs, int rowNum) throws SQLException {
        return new RobotTeam(rs.getInt(1),
                             rs.getString("teamname"),
                             rs.getString("ownerfirstname"),
                             rs.getString("ownerlastname"),
                             rs.getTimestamp("createDate").toLocalDateTime(),
                             rs.getTimestamp("lastUpdatedDate").toLocalDateTime());
    }
}
