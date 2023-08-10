package me.swamprat22.betterprojectiles;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        Entity shooter = (Entity) projectile.getShooter();

        if (shooter == null || shooter.getType() == EntityType.PLAYER) {
            Player player = (Player) projectile.getShooter();

            double yaw = Math.toRadians(player.getEyeLocation().getYaw());
            double pitch = Math.toRadians(player.getEyeLocation().getPitch());

            // Getting the position the player is looking at manually since I do not trust Bukkit to do it
            Vector direction = new Vector(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
            direction.normalize();

            switch(projectile.getType()) {
                case ENDER_PEARL:
                    // Speed up the pearl slightly (25% faster)
                    direction.multiply(1.25D);

                    /*
                      This will also speed up the pearl more depending on your velocity (Speed / BPS)
                      This gives pearls a very nice competitive aspect without being too overpowered
                     */
                    if (player.getVelocity().length() > 0D) {
                        direction.multiply(1.25D + player.getVelocity().length() / 10D);
                    }

                    // Making the pearl throw exactly in the direction you are looking
                    projectile.setVelocity(direction);

                    // Making it stay in air a bit longer before dropping
                    projectile.setFallDistance(0F);
                    break;

                case FISHING_HOOK:
                    // Speed up the pearl slightly (50% faster)
                    projectile.setVelocity(direction.multiply(1.5D));

                    // Making it stay in air a bit longer before dropping
                    projectile.setFallDistance(0F);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile.getType() == EntityType.ENDER_PEARL) {
            Entity shooter = (Entity)projectile.getShooter();

            if (shooter == null || shooter.getType() == EntityType.PLAYER) {
                Player player = (Player)shooter;

                /*
                 These lines are ensuring that the player will land at the right place (where the pearl lands)
                 Vanilla Minecraft has an issue with this where they trade accuracy for speed, so you may not land where you should have (This is faster and more accurate)
                 */
                player.getLocation().setX(projectile.getLocation().getX());
                player.getLocation().setY(projectile.getLocation().getY());
                player.getLocation().setZ(projectile.getLocation().getZ());
            }
        }
    }

    public void onDisable() {
    }
}
