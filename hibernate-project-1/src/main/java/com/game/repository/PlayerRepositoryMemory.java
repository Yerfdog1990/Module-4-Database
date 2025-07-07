package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository(value = "memory")
public class PlayerRepositoryMemory implements IPlayerRepository {

    private static final List<Player> storage = new CopyOnWriteArrayList<Player>() {{
        add(new Player(1L, "Nius", "Silent Arrival", Race.HOBBIT, Profession.ROGUE, new Date(1244497480000L), false, 33));
        add(new Player(2L, "Nikrashsh", "NightWolf", Race.ORC, Profession.WARRIOR, new Date(1152424240000L), false, 58));
        add(new Player(4L, "Ezzessel", "Hissing", Race.DWARF, Profession.CLERIC, new Date(1243201400000L), true, 3));
        add(new Player(5L, "Belan", "Tse Raa", Race.DWARF, Profession.ROGUE, new Date(1241378440000L), true, 29));
        add(new Player(6L, "Eleonora", "Grandma", Race.HUMAN, Profession.SORCERER, new Date(1214155000000L), true, 35));
        add(new Player(7L, "Eman", "Agile Soarer", Race.ELF, Profession.SORCERER, new Date(1214772360000L), false, 56));
        add(new Player(8L, "Talan", "Born in the Bronx", Race.GIANT, Profession.ROGUE, new Date(1217853300000L), false, 36));
        add(new Player(9L, "Arilan", "Benefactor", Race.ELF, Profession.SORCERER, new Date(1216022000000L), false, 34));
        add(new Player(10L, "Derakt", "Red-Eared Elf", Race.ELF, Profession.ROGUE, new Date(1215043720000L), false, 55));
        add(new Player(12L, "Archill", "Deadly", Race.GIANT, Profession.PALADIN, new Date(1243141000000L), false, 38));
        add(new Player(13L, "Endarion", "Little Elf", Race.ELF, Profession.DRUID, new Date(1219611120000L), false, 45));
        add(new Player(17L, "Faervin", "Dark Ideologue", Race.HUMAN, Profession.NAZGUL, new Date(1216967280000L), false, 12));
        add(new Player(18L, "Haridin", "Bedouin", Race.TROLL, Profession.WARRIOR, new Date(1211454600000L), false, 47));
        add(new Player(19L, "Joor", "Fighter with a Hunger", Race.ORC, Profession.DRUID, new Date(1211860680000L), false, 23));
        add(new Player(20L, "Gron", "Doomed Warrior", Race.GIANT, Profession.PALADIN, new Date(1218401160000L), false, 58));
        add(new Player(21L, "Morviel", "Spear of Kalima", Race.ELF, Profession.CLERIC, new Date(1244696560000L), false, 31));
        add(new Player(22L, "Nnufis", "Diamond", Race.HUMAN, Profession.ROGUE, new Date(1214556360000L), false, 56));
        add(new Player(25L, "Irkh", "Troll Bends Fir", Race.TROLL, Profession.WARRIOR, new Date(1244687920000L), true, 51));
        add(new Player(26L, "Blake", "Gray Warrior", Race.HUMAN, Profession.ROGUE, new Date(1242512800000L), false, 54));
        add(new Player(27L, "Ness", "Bead", Race.TROLL, Profession.WARRIOR, new Date(1216022000000L), true, 35));
        add(new Player(28L, "Ferin", "Warrior", Race.TROLL, Profession.WARRIOR, new Date(1216582000000L), false, 48));
        add(new Player(29L, "Solkah", "Apprentice of Magic", Race.ELF, Profession.SORCERER, new Date(1211843400000L), false, 54));
        add(new Player(30L, "Scink", "Titan of War", Race.GIANT, Profession.WARRIOR, new Date(1216315760000L), true, 41));
        add(new Player(32L, "Aisha", "Temptress", Race.HUMAN, Profession.CLERIC, new Date(1217761800000L), false, 45));
        add(new Player(33L, "Tant", "Devil Hide the Cotton", Race.DWARF, Profession.PALADIN, new Date(1214741200000L), false, 25));
        add(new Player(34L, "Trenigan", "Great Wizard", Race.ELF, Profession.SORCERER, new Date(1216020000000L), false, 42));
        add(new Player(35L, "Wudger", "Sorrowful", Race.TROLL, Profession.NAZGUL, new Date(1210163720000L), false, 42));
        add(new Player(36L, "Kamiraj", "Banker", Race.DWARF, Profession.CLERIC, new Date(1216454000000L), true, 39));
        add(new Player(41L, "Larkin", "Holy", Race.HOBBIT, Profession.CLERIC, new Date(1219740720000L), false, 46));
        add(new Player(42L, "Zandir", "Darkseer", Race.ELF, Profession.WARRIOR, new Date(1214850120000L), false, 23));
        add(new Player(43L, "Balgor", "Cave Uruk", Race.ORC, Profession.NAZGUL, new Date(1211597960000L), false, 18));
        add(new Player(44L, "Regarn", "Lover of Olivier Salad", Race.GIANT, Profession.WARRIOR, new Date(1197692680000L), false, 53));
        add(new Player(45L, "Anjelli", "Young Fighter", Race.DWARF, Profession.WARRIOR, new Date(1247890000000L), false, 33));
        add(new Player(23L, "Geris", "Imperial Warrior", Race.ORC, Profession.WARRIOR, new Date(1216630320000L), false, 58));
        add(new Player(46L, "Zheks", "Yarochka's Sun", Race.GIANT, Profession.WARRIOR, new Date(1214879560000L), false, 3));
        add(new Player(47L, "Philewel", "Chemist and Particle Physicist", Race.ELF, Profession.WARRIOR, new Date(1244445640000L), false, 30));
        add(new Player(48L, "Yara", "Alluring", Race.HUMAN, Profession.CLERIC, new Date(1241309300000L), false, 52));
        add(new Player(49L, "Illinas", "Hieroglyph", Race.HOBBIT, Profession.WARRIOR, new Date(1240834120000L), false, 47));
        add(new Player(50L, "Ardong", "Flash A", Race.HUMAN, Profession.WARRIOR, new Date(1217537160000L), false, 21));
        add(new Player(52L, "Attiris", "Acting Carvandos", Race.ELF, Profession.SORCERER, new Date(1245050800000L), true, 34));
    }};

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        return storage.stream()
                .sorted(Comparator.comparingLong(Player::getId))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    @Override
    public int getAllCount() {
        return storage.size();
    }

    @Override
    public Player save(Player player) {
        player.setId(getMaxId() + 1);
        storage.add(player);
        return player;
    }

    @Override
    public Player update(Player player) {
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {
        return storage.stream().filter(player -> id == player.getId()).findFirst();
    }

    @Override
    public void delete(Player player) {
        storage.remove(player);
    }

    private long getMaxId() {
        return storage.stream()
                .map(Player::getId)
                .max(Long::compareTo)
                .orElse(1L);
    }
}