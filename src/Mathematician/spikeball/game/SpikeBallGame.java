package Mathematician.spikeball.game;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.advancedparticles.AdvancedParticleGenerator;
import Mathematician.spikeball.advancedparticles.AdvancedParticleHandler;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import Mathematician.spikeball.gamemechanics.powerups.Freeze;
import Mathematician.spikeball.gamemechanics.powerups.PowerUp;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class SpikeBallGame {

    private SpikeBall spikeBall;
    private SpikeBallNet spikeBallNet;

    private SpikeBallTeam team1;
    private SpikeBallTeam team2;

    private HashMap<String, PowerUp[]> powerUps;
    private HashMap<String, Boolean> readyUp;

    private Objective scoreObjective;

    public enum GameStates {
        PRACTICE, PRE_MATCH, MATCH
    }

    public enum HitType {
        UP, SPIKING
    }

    private HashMap<String, HitType> hitTypeData;

    private GameStates currentState = GameStates.PRE_MATCH;

    private Scoreboard scoreboard;
    private ScoreboardManager scoreboardManager;

    private boolean redLastHit = true;
    private Player lastServedPlayer;
    private int hitCount = 0;
    private int bounceCount = 0;

    private final double SPIKE_BALL_GROUND_TOUCHING_TOLERANCE = 0.15;

    private int targetScore = 11;
    private int mustWinBy = 2;

    private boolean shouldVisualize = false;

    private PowerUp lastUsedPowerUp;

    private boolean isStartCancelled;

    public SpikeBallGame(SpikeBallNet spikeBallNet, ArrayList<Player> players){
        team1 = new SpikeBallTeam();
        team2 = new SpikeBallTeam();

        readyUp = new HashMap<>();

        hitTypeData = new HashMap<>();
        powerUps = new HashMap<>();

        isStartCancelled = true;

        this.spikeBallNet = spikeBallNet;
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();

        scoreObjective = scoreboard.registerNewObjective("Spike Ball Score", "dummy", ChatColor.DARK_GREEN + "Spike Ball Score");
        scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreObjective.setDisplayName(ChatColor.DARK_GREEN + "Spike Ball Score");

        Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
        redScore.setScore(0);

        Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
        blueScore.setScore(0);

        if(!(players.size() < 1)){
            for(Player p : players){
                addPlayer(p);
            }
            spikeBall = new SpikeBall(players.get(0).getWorld());
        }
    }

    public boolean getRedLastHit(){
        return redLastHit;
    }

    public void startGame(){
        if(numPlayers() == 4) {
            currentState = GameStates.MATCH;
            giveAllPlayersGameMaterials();
            Random random = new Random();
            int randomInt = random.nextInt(team1.getPlayers().size());
            spikeBall.giveToPlayer(team1.getPlayers().get(randomInt));
            redLastHit = true;
        } else {
            currentState = GameStates.PRACTICE;
            giveAllPlayersGameMaterials();
            if(team1.getPlayers().size() >= 1){
                spikeBall.giveToPlayer(team1.getPlayers().get(0));
                redLastHit = true;
            } else if(team2.getPlayers().size() >= 1) {
                spikeBall.giveToPlayer(team2.getPlayers().get(0));
                redLastHit = false;
            }
        }
        removeAllPlayerAdvancedParticles();
    }

    public void serveSpikeBall(Player player, Vector direction){
        spikeBall.setPlaying(true);
        lastServedPlayer = player;
        spikeBall.spawnInLocation(player.getLocation().add(player.getLocation().getDirection().normalize()).add(0,1.5,0), direction.normalize());
    }

    public void removePlayer(Player player){
        if(currentState == GameStates.MATCH || numPlayers() - 1 <= 0){
            messageAllPlayersInGame("Because " + player.getDisplayName() + " left the game, the game is cancelled!");
            terminateGame();
        } else {
            messageAllPlayersInGame(player.getDisplayName() + " left the game!");
            redTeam.remove(player);
            team2.getPlayers().remove(player);
            hitTypeData.remove(player.getDisplayName());
            powerUps.remove(player.getDisplayName());
            AdvancedParticleHandler.removeAdvancedParticleGenerator(player);
            player.setScoreboard(scoreboardManager.getNewScoreboard());
            giveBackPlayerInventory(player);
        }
    }

    public void safeRemovePlayer(Player player){
        if(currentState == GameStates.MATCH || numPlayers() - 1 <= 0){
            messageAllPlayersInGame("Because " + player.getDisplayName() + " left the game, the game is cancelled!");
            safeTerminateGame();
        } else {
            messageAllPlayersInGame(player.getDisplayName() + " left the game!");
            redTeam.remove(player);
            team2.getPlayers().remove(player);
            hitTypeData.remove(player.getDisplayName());
            powerUps.remove(player.getDisplayName());
            player.setScoreboard(scoreboardManager.getNewScoreboard());
            giveBackPlayerInventory(player);
        }
    }

    public void removeAllScoreboards(){
        for(Player p : redTeam){
            p.setScoreboard(scoreboardManager.getNewScoreboard());
        }

        for(Player player : team2.getPlayers()){
            player.setScoreboard(scoreboardManager.getNewScoreboard());
        }
    }

    public void changeSpikeBallVelocity(Vector velocity){
        spikeBall.setVelocity(velocity);
    }

    public boolean ifPlayerIsInGame(Player player){
        return onBlueTeam(player) || onRedTeam(player);
    }

    public SpikeBallNet getSpikeBallNet(){
        return spikeBallNet;
    }

    public boolean addPlayer(Player p){
        if(!ifPlayerIsInGame(p)) {
            if (redTeam.size() + team2.getPlayers().size() <= 3) {
                boolean joinedRed = false;
                if (redTeam.size() == 2) {
                    team2.getPlayers().add(p);
                    SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.BLUE + "Blue Team " + ChatColor.GOLD + ".");
                } else if (team2.getPlayers().size() == 2) {
                    redTeam.add(p);
                    joinedRed = true;
                    SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.RED + "Red Team " + ChatColor.GOLD + ".");
                } else {
                    Random random = new Random();
                    int randomNumber = random.nextInt(2);
                    if (randomNumber == 0) {
                        redTeam.add(p);
                        joinedRed = true;
                        SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.RED + "Red Team " + ChatColor.GOLD + ".");
                    } else {
                        team2.getPlayers().add(p);
                        SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.BLUE + "Blue Team " + ChatColor.GOLD + ".");
                    }
                }
                savePlayerInventory(p);
                readyUp.put(p.getDisplayName(), false);
                setPlayerInventoryToGameMaterials(p, joinedRed);
                setPowerUps(p);
                if(joinedRed){
                    messageAllPlayersInGame(p.getDisplayName() + " has joined on the " + ChatColor.RED + "Red Team" + ChatColor.GOLD + "!");
                } else {
                    messageAllPlayersInGame(p.getDisplayName() + " has joined on the " + ChatColor.BLUE + "Blue Team" + ChatColor.GOLD + "!");
                }
            } else {
                SpikeBallMain.sendPluginMessage(p, "Sorry, this game is full.");
            }
            p.setScoreboard(scoreboard);
            return true;
        }
        return false;
    }

    public void messageAllPlayersInGame(String message){
        for(Player p : redTeam){
            SpikeBallMain.sendPluginMessage(p, message);
        }
        for (Player c : blueTeam){
            SpikeBallMain.sendPluginMessage(c, message);
        }
    }

    public int numPlayers(){
        return redTeam.size() + blueTeam.size();
    }

    public void update(){
        switch (currentState){
            case MATCH:
                updateInPracticeOrMatchState();
                break;
            case PRACTICE:
                updateInPracticeOrMatchState();
                break;
            case PRE_MATCH:
                for(Player player : blueTeam){
                    if(readyUp.get(player.getDisplayName())){

                    }
                }
                break;
        }
    }

    public void updateInPracticeOrMatchState(){
        if(spikeBall.getEntity() != null && spikeBall.isPlaying()) {
            Particle.DustOptions dust;
            if(redLastHit){
                dust = new Particle.DustOptions(Color.fromRGB(219, 37, 37), 2);
            } else {
                dust = new Particle.DustOptions(Color.fromRGB(35, 32, 230), 2);
            }
            spikeBall.getLocation().getWorld().spawnParticle(Particle.REDSTONE, spikeBall.getLocation(), 1, dust);
            if (ifSpikeBallHitNet()) {
                spikeBall.setVelocity(new Vector(spikeBall.getEntity().getVelocity().getX(), Math.abs(spikeBall.getEntity().getVelocity().getY()), spikeBall.getEntity().getVelocity().getZ()));
                redLastHit = !redLastHit;
                hitCount = 0;
                if(hitCount == 0) {
                    bounceCount++;
                }
                if(bounceCount >= 2){
                    bounceCount = 0;
                    updateScore();
                    messageAllPlayersInGame("Stopped because bounceCount is greater than 2.");
                }
            } else if (!spikeBall.getEntity().getLocation().add(0, -SPIKE_BALL_GROUND_TOUCHING_TOLERANCE, 0).getBlock().getType().equals(Material.AIR)) {
                messageAllPlayersInGame("Stopped because it hit the ground");
                updateScore();
                bounceCount = 0;
            }
        }
    }

    public void clearSpikeBall(){
        spikeBall.removeSpikeBallFromWorld();
        for(Player p : redTeam){
            for(ItemStack itemStack : p.getInventory().getContents()){
                if(itemStack != null && itemStack.getItemMeta() != null & itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                    itemStack.setAmount(0);
                    break;
                }
            }
        }
        for(Player c : blueTeam){
            for(ItemStack itemStack : c.getInventory().getContents()){
                if(itemStack != null && itemStack.getItemMeta() != null & itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                    itemStack.setAmount(0);
                    break;
                }
            }
        }
    }

    public boolean isInProgress(){
        if(currentState == GameStates.MATCH||currentState == GameStates.PRACTICE){
            return true;
        }
        return false;
    }

    //Activates when a team fails
    public void updateScore(){
        if(redLastHit){
            Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
            blueScore.setScore(blueScore.getScore() + 1);
            if(blueTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(blueTeam.size());
                spikeBall.giveToPlayer(blueTeam.get(randomInt));
                redLastHit = false;
            } else if(lastServedPlayer != null){
                spikeBall.giveToPlayer(lastServedPlayer);
                redLastHit = false;
            } else if(redTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(redTeam.size());
                spikeBall.giveToPlayer(redTeam.get(randomInt));
                redLastHit = true;
            }
        } else {
            Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
            redScore.setScore(redScore.getScore() + 1);
            if(redTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(redTeam.size());
                spikeBall.giveToPlayer(redTeam.get(randomInt));
                redLastHit = true;
            } else if(lastServedPlayer != null){
                spikeBall.giveToPlayer(lastServedPlayer);
                redLastHit = true;
            } else if(blueTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(blueTeam.size());
                spikeBall.giveToPlayer(blueTeam.get(randomInt));
                redLastHit = false;
            }
        }
        hitCount = 0;
        spikeBall.removeSpikeBallFromWorld();
        if(teamWon()){
            Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
            Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
            String winningTeam = ChatColor.RED + "Red";
            if(blueScore.getScore() > redScore.getScore()){
                winningTeam = ChatColor.BLUE + "Blue";
            }
            messageAllPlayersInGame(winningTeam + " team won! Thanks for playing!");
            terminateGame();
        }
    }

    public boolean ifSpikeBallHitNet(){
        if(spikeBallNet.getSpikeBallDetectionBox().overlaps(spikeBall.getEntity().getBoundingBox()) && spikeBall.getEntity().getVelocity().getY() <= 0){
            return true;
        }
        return false;
    }

    public boolean teamWon(){
        Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
        Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
        return (redScore.getScore() >= targetScore || blueScore.getScore() >= targetScore) && (blueScore.getScore() >= redScore.getScore() + mustWinBy || redScore.getScore() >= blueScore.getScore() + mustWinBy);
    }

    public void addHit(){
        hitCount++;
    }

    public int getHitCount(){
        return hitCount;
    }

    public void addVelocityToSpikeBall(Vector velocity){
        if(spikeBall.getEntity() != null){
            spikeBall.getEntity().setVelocity(spikeBall.getEntity().getVelocity().add(velocity));
        }
    }

    public void cancelVisualization(){
        shouldVisualize = false;
    }

    public boolean visualizeSpikeBallNetBoundingBox(){
        if(!shouldVisualize){
            visualizeBoundingBox(spikeBallNet.getSpikeBallDetectionBox(),spikeBallNet.getSpikeBallNetLocation().getWorld());
            return true;
        } else {
            cancelVisualization();
            return false;
        }
    }
    public void visualizeBoundingBox(BoundingBox boundingBox, World world){
        shouldVisualize = true;
        new BukkitRunnable(){
            public void run() {
                Particle effect = Particle.VILLAGER_HAPPY;
                world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()), 1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()),1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()),1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()),1);

                world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()),1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()),1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()),1);
                world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()),1);
                if(!shouldVisualize){
                    cancel();
                }
            }
        }.runTaskTimer(SpikeBallMain.plugin,0L, 10L);
    }

    //Does not remove cooldowns, so any reasons to shut down the game would not cause a ConcurrentModificationException
    public void safeTerminateGame(){
        removeAllScoreboards();
        clearSpikeBall();
        giveAllPlayersBackInventories();
        shouldVisualize = false;
        SpikeBallGameHandler.removeGame(this);
    }

    public void terminateGame(){
        removeAllScoreboards();
        clearSpikeBall();
        removeCooldowns();
        giveAllPlayersBackInventories();
        shouldVisualize = false;
        removeAllPlayerAdvancedParticles();
        SpikeBallGameHandler.removeGame(this);
    }

    public void removeCooldowns(){
        for(Player player : redTeam){
            CooldownHandler.removeCooldown(player);
        }

        for(Player player : blueTeam){
            CooldownHandler.removeCooldown(player);
        }
    }

    public void savePlayerInventory(Player player){
        inventories.put(player.getDisplayName(), player.getInventory().getContents());
    }

    public void clearPlayerInventory(Player player){
        player.getInventory().setContents(new ItemStack[player.getInventory().getSize()]);
    }

    public void setPlayerInventoryToGameMaterials(Player player, boolean ifRed){
        clearPlayerInventory(player);
        setPlayerTeamArmor(player, ifRed);
        setMatchStateMaterials(player,ifRed);
    }

    public void setPlayerTeamArmor(Player player, boolean ifRed){
        if(ifRed){
            ItemStack[] coloredArmor = new ItemStack[4];

            ItemStack helmet = new ItemStack(Material.RED_CONCRETE, 1);
            ItemMeta helmetMeta = helmet.getItemMeta();
            helmetMeta.setDisplayName(ChatColor.RED + "Spike Ball Red Team Helmet");
            helmet.setItemMeta(helmetMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta chestPlateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestPlateMeta.setColor(Color.RED);
            chestPlateMeta.setDisplayName(ChatColor.RED + "Spike Ball Red Team Chestplate");
            chestplate.setItemMeta(chestPlateMeta);

            ItemStack legging = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            LeatherArmorMeta leggingMeta = (LeatherArmorMeta) legging.getItemMeta();
            leggingMeta.setColor(Color.RED);
            leggingMeta.setDisplayName(ChatColor.RED + "Spike Ball Red Team Leggings");
            legging.setItemMeta(leggingMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(Color.RED);
            bootsMeta.setDisplayName(ChatColor.RED + "Spike Ball Red Team Boots");
            boots.setItemMeta(bootsMeta);

            coloredArmor[0] = boots;
            coloredArmor[1] = legging;
            coloredArmor[2] = chestplate;
            coloredArmor[3] = helmet;

            player.getInventory().setArmorContents(coloredArmor);

        } else {
            ItemStack[] coloredArmor = new ItemStack[4];

            ItemStack helmet = new ItemStack(Material.BLUE_CONCRETE, 1);
            ItemMeta helmetMeta = helmet.getItemMeta();
            helmetMeta.setDisplayName(ChatColor.BLUE + "Spike Ball Blue Team Helmet");
            helmet.setItemMeta(helmetMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta chestPlateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestPlateMeta.setColor(Color.BLUE);
            chestPlateMeta.setDisplayName(ChatColor.BLUE + "Spike Ball Blue Team Chestplate");
            chestplate.setItemMeta(chestPlateMeta);

            ItemStack legging = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            LeatherArmorMeta leggingMeta = (LeatherArmorMeta) legging.getItemMeta();
            leggingMeta.setColor(Color.BLUE);
            leggingMeta.setDisplayName(ChatColor.BLUE + "Spike Ball Blue Team Leggings");
            legging.setItemMeta(leggingMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(Color.BLUE);
            bootsMeta.setDisplayName(ChatColor.BLUE + "Spike Ball Blue Team Boots");
            boots.setItemMeta(bootsMeta);

            coloredArmor[0] = boots;
            coloredArmor[1] = legging;
            coloredArmor[2] = chestplate;
            coloredArmor[3] = helmet;

            player.getInventory().setArmorContents(coloredArmor);
        }
    }

    public void setMatchStateMaterials(Player player, boolean ifRed){
        if(currentState == GameStates.PRE_MATCH){
            ItemStack blueConcrete = new ItemStack(Material.BLUE_CONCRETE, 1);
            ItemStack redConcrete = new ItemStack(Material.RED_CONCRETE, 1);

            ItemMeta blueConcreteMeta = blueConcrete.getItemMeta();
            blueConcreteMeta.setDisplayName(ChatColor.GOLD + "Click to select to be on the " + ChatColor.BLUE + "Blue Team" + ChatColor.GOLD + ".");
            blueConcrete.setItemMeta(blueConcreteMeta);

            ItemMeta redConcreteMeta = redConcrete.getItemMeta();
            redConcreteMeta.setDisplayName(ChatColor.GOLD + "Click to select to be on the " + ChatColor.RED + "Red Team" + ChatColor.GOLD + ".");
            redConcrete.setItemMeta(redConcreteMeta);
            player.getInventory().setItem(0, blueConcrete);
            player.getInventory().setItem(1, redConcrete);

            ItemStack leaveClock = new ItemStack(Material.CLOCK, 1);
            ItemMeta leaveClockMeta = leaveClock.getItemMeta();
            leaveClockMeta.setDisplayName(ChatColor.RED + "Match Leaving Clock" + ChatColor.GRAY + " (Right Click)");
            leaveClock.setItemMeta(leaveClockMeta);

            player.getInventory().setItem(8, leaveClock);

            setReadyItems(player);

        } else {
            if(ifRed){
                ItemStack redConcrete = new ItemStack(Material.RED_CONCRETE, 1);
                ItemMeta redConcreteMeta = redConcrete.getItemMeta();
                redConcreteMeta.setDisplayName(ChatColor.GREEN + "Up Mode");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.RED + "Drop to Change Hit Mode");
                redConcreteMeta.setLore(lore);
                redConcrete.setItemMeta(redConcreteMeta);
                player.getInventory().setItem(0, redConcrete);
            } else {
                ItemStack blueConcrete = new ItemStack(Material.BLUE_CONCRETE, 1);
                ItemMeta blueConcreteMeta = blueConcrete.getItemMeta();
                blueConcreteMeta.setDisplayName(ChatColor.GREEN + "Up Mode");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.BLUE + "Drop to Change Hit Mode");
                blueConcreteMeta.setLore(lore);
                blueConcrete.setItemMeta(blueConcreteMeta);
                player.getInventory().setItem(0, blueConcrete);
            }
            hitTypeData.put(player.getDisplayName(), HitType.UP);
            givePowerUpsToPlayer(player);
        }
    }

    public void giveBackPlayerInventory(Player player){
        if(inventories.containsKey(player.getDisplayName())){
            player.getInventory().setContents(inventories.get(player.getDisplayName()));
            inventories.remove(player.getDisplayName());
        }
    }

    public void giveAllPlayersBackInventories(){
        for(Player player : blueTeam){
            giveBackPlayerInventory(player);
        }

        for(Player player : redTeam){
            giveBackPlayerInventory(player);
        }
    }

    public boolean switchTeams(Player p, boolean ifRed){
        if(ifRed){
            if(onRedTeam(p) || redTeam.size() > 1){
                return false;
            }
            redTeam.add(p);
            blueTeam.remove(p);
            messageAllPlayersInGame(p.getDisplayName() + " has joined on the " + ChatColor.RED + "Red Team" + ChatColor.GOLD + "!");
        } else {
            if(onBlueTeam(p) || blueTeam.size() > 1){
                return false;
            }

            blueTeam.add(p);
            redTeam.remove(p);
            messageAllPlayersInGame(p.getDisplayName() + " has joined on the " + ChatColor.BLUE + "Blue Team" + ChatColor.GOLD + "!");
        }
        setPlayerTeamArmor(p, ifRed);
        return true;
    }

    public boolean onBlueTeam(Player player){
        for(Player p : blueTeam){
            if(player.equals(p)){
                return true;
            }
        }
        return false;
    }

    public boolean onRedTeam(Player player){
        for(Player p : redTeam){
            if(player.equals(p)){
                return true;
            }
        }
        return false;
    }

    public void switchTypeOfHit(Player player, ItemStack droppedItem){
        if(hitTypeData.containsKey(player.getDisplayName())){
            HitType hitType = hitTypeData.get(player.getDisplayName());
            ItemMeta hitSelectorMeta = droppedItem.getItemMeta();

            switch (hitType){
                case UP:
                    hitTypeData.put(player.getDisplayName(), HitType.SPIKING);
                    hitSelectorMeta.setDisplayName(ChatColor.GOLD + "Spike Mode");
                    break;

                case SPIKING:
                    hitTypeData.put(player.getDisplayName(), HitType.UP);
                    hitSelectorMeta.setDisplayName(ChatColor.GREEN + "Up Mode");
                    break;
            }

            droppedItem.setItemMeta(hitSelectorMeta);
        }
    }

    public HitType getHitType(Player player){
        if(hitTypeData.containsKey(player.getDisplayName())){
            return hitTypeData.get(player.getDisplayName());
        }
        return null;
    }

    public void giveAllPlayersGameMaterials(){
        for(Player p : redTeam){
            setPlayerInventoryToGameMaterials(p, true);
        }

        for(Player player : blueTeam){
            setPlayerInventoryToGameMaterials(player, false);
        }
    }

    public SpikeBall getSpikeBall(){
        return spikeBall;
    }

    public void setPowerUps(Player player){
        PowerUp[] powerUps = new PowerUp[1];
        powerUps[0] = new Freeze(player, this);
        this.powerUps.put(player.getDisplayName(),powerUps);
    }

    public PowerUp[] getPowerUps(Player player){
        if(powerUps.containsKey(player.getDisplayName())) {
            return powerUps.get(player.getDisplayName());
        }
        return null;
    }

    public void givePowerUpsToPlayer(Player player){
        PowerUp[] powerUps = this.powerUps.get(player.getDisplayName());
        if(powerUps != null) {
            for (int i = 0; i < powerUps.length; i++) {
                player.getInventory().setItem(i + 1, powerUps[i].getItemStack());
            }
        }
    }

    public void setLastUsedPowerUp(PowerUp powerUp){
        lastUsedPowerUp = powerUp;
    }

    public boolean isIfPowerUpGoingOn(){
        return lastUsedPowerUp != null;
    }

    public PowerUp getLastUsedPowerUp(){
        return lastUsedPowerUp;
    }

    public void makePlayerReady(Player player){
        readyUp.put(player.getDisplayName(), true);
        setReadyItems(player);
        AdvancedParticleHandler.addAdvancedParticleGenerator(new AdvancedParticleGenerator(player,(t) -> {
            Vector output = new Vector();
            double a = 1;
            double b = 2.0/5.0;
            double h = 1.2;

            output.setX((a + b)*Math.cos(t) - h*Math.cos((a + b)/b*t));
            output.setY((a + b)*Math.sin(t) - h*Math.sin((a + b)/b*t));
            output.setZ(0);
            return output;
        },0,12.6,10,0.1,Particle.VILLAGER_HAPPY, true));
        if(areAllPlayersReady()){
            isStartCancelled = false;
            long startTime = System.currentTimeMillis();
            long finishTime = startTime + 3000;
            new BukkitRunnable(){
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    if(currentTime >= finishTime){
                        startGame();
                        isStartCancelled = true;
                    } else {
                        messageAllPlayersInGame("The game will begin in " + ChatColor.AQUA + (int) Math.ceil((finishTime - currentTime) / 1000.0) + ChatColor.GOLD + " seconds.");
                    }
                    if(isStartCancelled){
                        cancel();
                    }
                }
            }.runTaskTimer(SpikeBallMain.plugin,0L, 20L);
        }
    }

    public void setReadyItems(Player player){
        if(readyUp.containsKey(player.getDisplayName()) && !readyUp.get(player.getDisplayName())) {
            ItemStack readyItem = new ItemStack(Material.RED_TERRACOTTA, 1);
            ItemMeta readyItemMeta = readyItem.getItemMeta();
            readyItemMeta.setDisplayName(ChatColor.RED + "You are not ready to start!" + ChatColor.GRAY + " (Right Click)");
            readyItem.setItemMeta(readyItemMeta);

            player.getInventory().setItem(7, readyItem);
        } else {
            ItemStack readyItem = new ItemStack(Material.GREEN_TERRACOTTA, 1);
            ItemMeta readyItemMeta = readyItem.getItemMeta();
            readyItemMeta.setDisplayName(ChatColor.GREEN + "You are ready to start!" + ChatColor.GRAY + " (Right Click)");
            readyItem.setItemMeta(readyItemMeta);

            player.getInventory().setItem(7, readyItem);
        }
    }

    public void makePlayerNotReady(Player player){
        readyUp.put(player.getDisplayName(), false);
        setReadyItems(player);
        isStartCancelled = true;
        AdvancedParticleHandler.removeAdvancedParticleGenerator(player);
    }

    public boolean areAllPlayersReady(){
        return !readyUp.containsValue(false);
    }

    public void removeAllPlayerAdvancedParticles(){
        for(Player player : team2.getPlayers()){
            AdvancedParticleHandler.removeAdvancedParticleGenerator(player);
        }

        for(Player player : redTeam){
            AdvancedParticleHandler.removeAdvancedParticleGenerator(player);
        }
    }

}
