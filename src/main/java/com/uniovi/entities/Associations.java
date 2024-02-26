package com.uniovi.entities;

public class Associations {
    public static class PlayerRole {
        /**
         * Add a new association between a player and a role
         * @param player The player
         * @param role The role
         */
        public static void addRole(Player player, Role role) {
            role.getPlayers().add(player);
            player.getRoles().add(role);
        }

        /**
         * Remove an association between a player and a role
         * @param player The player
         * @param role The role
         */
        public static void removeRole(Player player, Role role) {
            role.getPlayers().remove(player);
            player.getRoles().remove(role);
        }
    }
}
