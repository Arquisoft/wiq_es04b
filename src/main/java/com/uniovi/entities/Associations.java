package com.uniovi.entities;

import java.util.*;

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
            apiKey.setPlayer(player);
            player.setApiKey(apiKey);
        }

        /**
         * Remove an association between a player and an API key
         *
         * @param player The player
         * @param apiKey The API key
         */
        public static void removeApiKey(Player player, ApiKey apiKey) {
            apiKey.setPlayer(null);
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

    public static class PlayerGameSession {
        /**
         * Add a new association between a player and a game session
         *
         * @param player      The player
         * @param gameSession The game session
         */

        public static void addGameSession(Player player, GameSession gameSession) {
            gameSession.setPlayer(player);
            player.getGameSessions().add(gameSession);
        }

        /**
         * Remove an association between a player and a game session
         *
         * @param player      The player
         * @param gameSession The game session
         */
        public static void removeGameSession(Player player, GameSession gameSession) {
            player.getGameSessions().remove(gameSession);
            gameSession.setPlayer(null);
        }
    }

    public static class QuestionAnswers {
        /**
         * Add a new association between a question and an answer
         *
         * @param question The question
         * @param answer   The answer
         */
        public static void addAnswer(Question question, List<Answer> answer) {
            for (Answer a : answer) {
                a.setQuestion(question);
            }
            question.getOptions().addAll(answer);
        }

        /**
         * Remove an association between a question and an answer
         *
         * @param question The question
         * @param answer   The answer
         */
        public static void removeAnswer(Question question, List<Answer> answer) {
            question.getOptions().removeAll(answer);
            for (Answer a : answer) {
                a.setQuestion(null);
            }
        }
        //public static void removeAnswer(Question question, List<Answer> answer) {
          //  question.getOptions().remove(answer);
            //for (Answer a : answer) {
              //  a.setQuestion(null);
            //}
        //}
    }
}
