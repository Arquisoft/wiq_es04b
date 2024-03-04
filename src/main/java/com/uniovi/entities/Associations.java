package com.uniovi.entities;

public class Associations {
    public static class PlayerRole {
        /**
         * Add a new association between a player and a role
         *
         * @param player The player
         * @param role   The role
         */
        public static void addRole(Player player, Role role) {
            role.getPlayers().add(player);
            player.getRoles().add(role);
        }

        /**
         * Remove an association between a player and a role
         *
         * @param player The player
         * @param role   The role
         */
        public static void removeRole(Player player, Role role) {
            role.getPlayers().remove(player);
            player.getRoles().remove(role);
        }
    }

    public static class PlayerApiKey {
        /**
         * Add a new association between a player and an API key
         *
         * @param player The player
         * @param apiKey The API key
         */
        public static void addApiKey(Player player, ApiKey apiKey) {
            apiKey.setUser(player);
            player.setApiKey(apiKey);
        }

        /**
         * Remove an association between a player and an API key
         *
         * @param player The player
         * @param apiKey The API key
         */
        public static void removeApiKey(Player player, ApiKey apiKey) {
            apiKey.setUser(null);
            player.setApiKey(null);
        }
    }

    public static class ApiKeyAccessLog {
        /**
         * Add a new association between an API key and an access log
         *
         * @param apiKey    The API key
         * @param accessLog The access log
         */
        public static void addAccessLog(ApiKey apiKey, RestApiAccessLog accessLog) {
            accessLog.setApiKey(apiKey);
            apiKey.getAccessLogs().add(accessLog);
        }

        /**
         * Remove an association between an API key and an access log
         *
         * @param apiKey    The API key
         * @param accessLog The access log
         */
        public static void removeAccessLog(ApiKey apiKey, RestApiAccessLog accessLog) {
            apiKey.getAccessLogs().remove(accessLog);
            accessLog.setApiKey(null);
        }
    }
}
