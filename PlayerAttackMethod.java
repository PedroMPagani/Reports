    public void attack(Entity target) {
        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(this)) {
                float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float f1;

                if (target instanceof LivingEntity) {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) target).getMobType());
                } else {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
                }

                float f2 = this.getAttackStrengthScale(0.5F);

                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                // this.resetAttackCooldown(); // CraftBukkit - Moved to EntityLiving to reset the cooldown after the damage is dealt
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    byte b0 = 0;
                    int i = b0 + EnchantmentHelper.getKnockbackBonus(this);

                    if (this.isSprinting() && flag) {
                        sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && target instanceof LivingEntity; // Paper - Add critical damage API - conflict on change

                    flag2 = flag2 && !level.paperConfig.disablePlayerCrits; // Paper
                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.walkDist - this.walkDistO);

                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.getSpeed()) {
                        ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof SwordItem) {
                            flag3 = true;
                        }
                    }

                    float f3 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentHelper.getFireAspect(this);

                    if (target instanceof LivingEntity) {
                        f3 = ((LivingEntity) target).getHealth();
                        if (j > 0 && !target.isOnFire()) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), target.getBukkitEntity(), 1);
                            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                            if (!combustEvent.isCancelled()) {
                                flag4 = true;
                                target.setSecondsOnFire(combustEvent.getDuration(), false);
                            }
                            // CraftBukkit end
                        }
                    }

                    Vec3 vec3d = target.getDeltaMovement();
                    boolean flag5 = target.hurt(DamageSource.playerAttack(this).critical(flag2), f); // Paper - add critical damage API

                    if (flag5) {
                        if (i > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity) target).knockback((double) ((float) i * 0.5F), (double) Mth.sin(this.getYRot() * 0.017453292F), (double) (-Mth.cos(this.getYRot() * 0.017453292F)), this); // Paper
                            } else {
                                target.push((double) (-Mth.sin(this.getYRot() * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (Mth.cos(this.getYRot() * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            // Paper start - Configuration option to disable automatic sprint interruption
                            if (!level.paperConfig.disableSprintInterruptionOnAttack) {
                                this.setSprinting(false);
                            }
                            // Paper end
                        }

                        if (flag3) {
                            float f4 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;
                            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D));
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                LivingEntity entityliving = (LivingEntity) iterator.next();

                                if (entityliving != this && entityliving != target && !this.isAlliedTo((Entity) entityliving) && (!(entityliving instanceof ArmorStand) || !((ArmorStand) entityliving).isMarker()) && this.distanceToSqr((Entity) entityliving) < 9.0D) {
                                    // CraftBukkit start - Only apply knockback if the damage hits
                                    if (entityliving.hurt(DamageSource.playerAttack(this).sweep().critical(flag2), f4)) { // Paper - add critical damage API
                                    entityliving.knockback(0.4000000059604645D, (double) Mth.sin(this.getYRot() * 0.017453292F), (double) (-Mth.cos(this.getYRot() * 0.017453292F)), this); // Paper
                                    }
                                    // CraftBukkit end
                                }
                            }

                            sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                            this.sweepAttack();
                        }

                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            // CraftBukkit start - Add Velocity Event
                            boolean cancelled = false;
                            org.bukkit.entity.Player player = (org.bukkit.entity.Player) target.getBukkitEntity();
                            org.bukkit.util.Vector velocity = CraftVector.toBukkit(vec3d);

                            PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                            level.getCraftServer().getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                cancelled = true;
                            } else if (!velocity.equals(event.getVelocity())) {
                                player.setVelocity(event.getVelocity());
                            }

                            if (!cancelled) {
                            ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3d);
                            }
                            // CraftBukkit end
                        }

                        if (flag2) {
                            sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                            this.crit(target);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                            } else {
                                sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                            }
                        }

                        if (f1 > 0.0F) {
                            this.magicCrit(target);
                        }

                        this.setLastHurtMob(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) target, this);
                        }

                        EnchantmentHelper.doPostDamageEffects(this, target);
                        ItemStack itemstack1 = this.getMainHandItem();
                        Object object = target;

                        if (target instanceof EnderDragonPart) {
                            object = ((EnderDragonPart) target).parentMob;
                        }

                        if (!this.level.isClientSide && !itemstack1.isEmpty() && object instanceof LivingEntity) {
                            itemstack1.hurtEnemy((LivingEntity) object, this);
                            if (itemstack1.isEmpty()) {
                                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (target instanceof LivingEntity) {
                            float f5 = f3 - ((LivingEntity) target).getHealth();

                            this.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                                EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), target.getBukkitEntity(), j * 4);
                                org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                                if (!combustEvent.isCancelled()) {
                                    target.setSecondsOnFire(combustEvent.getDuration(), false);
                                }
                                // CraftBukkit end
                            }

                            if (this.level instanceof ServerLevel && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);

                                ((ServerLevel) this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.causeFoodExhaustion(level.spigotConfig.combatExhaustion, EntityExhaustionEvent.ExhaustionReason.ATTACK); // CraftBukkit - EntityExhaustionEvent // Spigot - Change to use configurable value
                    } else {
                        sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F); // Paper - send while respecting visibility
                        if (flag4) {
                            target.clearFire();
                        }
                        // CraftBukkit start - resync on cancelled event
                        if (this instanceof ServerPlayer) {
                            ((ServerPlayer) this).getBukkitEntity().updateInventory();
                        }
                        // CraftBukkit end
                    }
                }

            }
        }
    }

    public void knockback(double strength, double x, double z) {
        // Paper start - add knockbacking entity parameter
        this.knockback(strength, x, z, null);
    }

    public void knockback(double strength, double x, double z, Entity knockingBackEntity) {
        // Paper end - add knockbacking entity parameter
        strength *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (strength > 0.0D) {
            this.hasImpulse = true;
            Vec3 vec3d = this.getDeltaMovement();
            Vec3 vec3d1 = (new Vec3(x, 0.0D, z)).normalize().scale(strength);

            this.setDeltaMovement(vec3d.x / 2.0D - vec3d1.x, this.onGround ? Math.min(0.4D, vec3d.y / 2.0D + strength) : vec3d.y, vec3d.z / 2.0D - vec3d1.z);
            // Paper start - call EntityKnockbackByEntityEvent
            Vec3 currentMovement = this.getDeltaMovement();
            org.bukkit.util.Vector delta = new org.bukkit.util.Vector(currentMovement.x - vec3d.x, currentMovement.y - vec3d.y, currentMovement.z - vec3d.z);
            // Restore old velocity to be able to access it in the event
            this.setDeltaMovement(vec3d);
            if (knockingBackEntity == null || new com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent((org.bukkit.entity.LivingEntity) getBukkitEntity(), knockingBackEntity.getBukkitEntity(), (float) strength, delta).callEvent()) {
                this.setDeltaMovement(vec3d.x + delta.getX(), vec3d.y + delta.getY(), vec3d.z + delta.getZ());
            }
            // Paper end
        }
    }


    # Entity.class
    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }

    public void setDeltaMovement(Vec3 velocity) {
        synchronized (this.posLock) { // Paper
        this.deltaMovement = velocity;
        } // Paper
    }
