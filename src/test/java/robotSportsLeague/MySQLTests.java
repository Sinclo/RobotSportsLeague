package robotSportsLeague;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.junit.Assert;
import robotSportsLeague.config.JPAConfig;
import robotSportsLeague.db.JdbcRobotTeamRepository;
import robotSportsLeague.web.model.RobotTeam;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JPAConfig.class}, loader = AnnotationConfigContextLoader.class)
@DataJpaTest
public class MySQLTests {

    @Autowired
    private JdbcTemplate jdbc = new JdbcTemplate();

    @Resource
    JdbcRobotTeamRepository jdbcRobotTeamRepo = new JdbcRobotTeamRepository(jdbc);

    @Test
    public void insertNewEntryAndVerifyData(){

        String expectedTeamName = "Team Name";
        String expectedFirstName = "First Name";
        String expectedLastName = "Last Name";

        RobotTeam insertEntry = new RobotTeam();
        insertEntry.setTeamName(expectedTeamName);
        insertEntry.setOwnerFirstName(expectedFirstName);
        insertEntry.setOwnerLastName(expectedLastName);
        jdbcRobotTeamRepo.save(insertEntry);

        Assert.assertNotNull(jdbcRobotTeamRepo.findOne(expectedTeamName).getId());
        Assert.assertEquals(expectedTeamName, jdbcRobotTeamRepo.findOne(expectedTeamName).getTeamName());
        Assert.assertEquals(expectedFirstName, jdbcRobotTeamRepo.findOne(expectedTeamName).getOwnerFirstName());
        Assert.assertEquals(expectedLastName, jdbcRobotTeamRepo.findOne(expectedTeamName).getOwnerLastName());
        Assert.assertNotNull(jdbcRobotTeamRepo.findOne(expectedTeamName).getCreateDate());
        Assert.assertNotNull(jdbcRobotTeamRepo.findOne(expectedTeamName).getLastUpdatedDate());
    }
    @Test(expected = DuplicateKeyException.class)
    public void preventDuplicateTeamNames(){

        String expectedTeamName = "Team Name";
        String expectedFirstName1 = "First Name";
        String expectedLastName1 = "Last Name";
        String expectedFirstName2 = "2nd First Name";
        String expectedLastName2 = "2nd Last Name";

        RobotTeam insertEntry = new RobotTeam();

        // Create first team with team owner
        insertEntry.setTeamName(expectedTeamName);
        insertEntry.setOwnerFirstName(expectedFirstName1);
        insertEntry.setOwnerLastName(expectedLastName1);
        jdbcRobotTeamRepo.save(insertEntry);

        // Create second team with different team owner, but same team name.
        // Assert DuplicateKeyException response is returned
        insertEntry.setTeamName(expectedTeamName);
        insertEntry.setOwnerFirstName(expectedFirstName2);
        insertEntry.setOwnerLastName(expectedLastName2);
        jdbcRobotTeamRepo.save(insertEntry);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void preventNullTeamName(){
        RobotTeam insertEntry = new RobotTeam();
        insertEntry.setTeamName(null);
        insertEntry.setOwnerFirstName("First Name");
        insertEntry.setOwnerLastName("Last Name");
        jdbcRobotTeamRepo.save(insertEntry);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void preventNullFirstName(){
        RobotTeam insertEntry = new RobotTeam();
        insertEntry.setTeamName("Team Name");
        insertEntry.setOwnerFirstName(null);
        insertEntry.setOwnerLastName("Last Name");
        jdbcRobotTeamRepo.save(insertEntry);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void preventNullLastName(){
        RobotTeam insertEntry = new RobotTeam();
        insertEntry.setTeamName("Team Name");
        insertEntry.setOwnerFirstName("First Name");
        insertEntry.setOwnerLastName(null);
        jdbcRobotTeamRepo.save(insertEntry);
    }

    @Test
    public void createDateOnInsert(){
        // Get current date & time
        LocalDateTime dateTime = LocalDateTime.now();

        // Reformat current date & time to exclude nanoseconds
        LocalDateTime dateTimeFormat = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(),
                dateTime.getDayOfMonth(), dateTime.getHour(),
                dateTime.getMinute(), dateTime.getSecond());

        // Execute INSERT query to database
        RobotTeam insertEntry = new RobotTeam();
        insertEntry.setTeamName("Team Name");
        insertEntry.setOwnerFirstName("First Name");
        insertEntry.setOwnerLastName("Last Name");

        jdbcRobotTeamRepo.save(insertEntry);

        // Retrieve data from 'createdate' column of the INSERT query above
        String getInsertedTeamName = insertEntry.getTeamName();
        String getInsertedCreateDate = jdbcRobotTeamRepo.findOne(getInsertedTeamName).getCreateDate().toString();

        // Verify that createDate was generated upon insert (excluding nanoseconds)
        Assert.assertTrue("'createdate' is invalid", getInsertedCreateDate.contains(dateTimeFormat.toString()));
    }

    @Test
    public void updateDateOnUpdate() throws InterruptedException{

        //Create new database entry
        RobotTeam robotTeam = new RobotTeam();
        robotTeam.setTeamName("Team Name");
        robotTeam.setOwnerFirstName("First Name");
        robotTeam.setOwnerLastName("Last Name");
        jdbcRobotTeamRepo.save(robotTeam);

        // Retrieve 'createDate' and 'lastUpdated' values from the INSERT entry above
        String insertedTeamName = robotTeam.getTeamName();
        LocalDateTime createDate1 = jdbcRobotTeamRepo.findOne(insertedTeamName).getCreateDate();
        LocalDateTime updateDate1 = jdbcRobotTeamRepo.findOne(insertedTeamName).getLastUpdatedDate();

        // Wait for a little while
        Thread.sleep(1500);

        // Get new update date & time
        LocalDateTime expectedUpdateDate = LocalDateTime.now();

        robotTeam.setOwnerFirstName("New First Name");
        robotTeam.setOwnerLastName("New Last Name");
        robotTeam.setLastUpdatedDate(expectedUpdateDate);
        jdbcRobotTeamRepo.update(robotTeam);

        // Retrieve 'createDate' and 'lastUpdated' values from the UPDATE entry above
        LocalDateTime createDate2 = jdbcRobotTeamRepo.findOne(insertedTeamName).getCreateDate();
        LocalDateTime updateDate2 = jdbcRobotTeamRepo.findOne(insertedTeamName).getLastUpdatedDate();

        // Verify that createDate remained the same, but that new updateDate was generated upon update
        Assert.assertEquals("'createdate' is invalid", createDate1, createDate2);
        Assert.assertTrue("'lastupdateddate' is invalid", updateDate2.isAfter(updateDate1));
    }
}
