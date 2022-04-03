    public void d(Entity entity) {
        if (entity.cf() && !entity.r(this)) {
            float f = (float)this.b((AttributeBase)GenericAttributes.f);
            float f1;
            if (entity instanceof EntityLiving) {
                f1 = EnchantmentManager.a(this.es(), ((EntityLiving)entity).er());
            } else {
                f1 = EnchantmentManager.a(this.es(), EnumMonsterType.a);
            }

            float f2 = this.v(0.5F);
            f *= 0.2F + f2 * f2 * 0.8F;
            f1 *= f2;
            if (f > 0.0F || f1 > 0.0F) {
                boolean flag = f2 > 0.9F;
                boolean flag1 = false;
                byte b0 = 0;
                int i = b0 + EnchantmentManager.b(this);
                if (this.bO() && flag) {
                    this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.ox, this.cO(), 1.0F, 1.0F);
                    ++i;
                    flag1 = true;
                }

                boolean flag2 = flag && this.L > 0.0F && !this.y && !this.b_() && !this.aQ() && !this.a((MobEffectList)MobEffects.o) && !this.bF() && entity instanceof EntityLiving;
                flag2 = flag2 && !this.bO();
                if (flag2) {
                    f *= 1.5F;
                }

                f += f1;
                boolean flag3 = false;
                double d0 = (double)(this.I - this.H);
                if (flag && !flag2 && !flag1 && this.y && d0 < (double)this.eD()) {
                    ItemStack itemstack = this.b((EnumHand)EnumHand.a);
                    if (itemstack.c() instanceof ItemSword) {
                        flag3 = true;
                    }
                }

                float f3 = 0.0F;
                boolean flag4 = false;
                int j = EnchantmentManager.c(this);
                if (entity instanceof EntityLiving) {
                    f3 = ((EntityLiving)entity).ea();
                    if (j > 0 && !entity.bE()) {
                        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 1);
                        Bukkit.getPluginManager().callEvent(combustEvent);
                        if (!combustEvent.isCancelled()) {
                            flag4 = true;
                            entity.setSecondsOnFire(combustEvent.getDuration(), false);
                        }
                    }
                }

                Vec3D vec3d = entity.da();
                boolean flag5 = entity.a(DamageSource.a(this), f);
                if (flag5) {
                    if (i > 0) {
                        if (entity instanceof EntityLiving) {
                            ((EntityLiving)entity).p((double)((float)i * 0.5F), (double)MathHelper.a(this.dn() * 0.017453292F), (double)(-MathHelper.b(this.dn() * 0.017453292F)));
                        } else {
                            entity.i((double)(-MathHelper.a(this.dn() * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.b(this.dn() * 0.017453292F) * (float)i * 0.5F));
                        }

                        this.g((Vec3D)this.da().d(0.6D, 1.0D, 0.6D));
                        this.g(false);
                    }

                    if (flag3) {
                        float f4 = 1.0F + EnchantmentManager.a(this) * f;
                        List<EntityLiving> list = this.s.a(EntityLiving.class, entity.cw().c(1.0D, 0.25D, 1.0D));
                        Iterator iterator = list.iterator();

                        label179:
                        while(true) {
                            EntityLiving entityliving;
                            do {
                                do {
                                    do {
                                        do {
                                            if (!iterator.hasNext()) {
                                                this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.oA, this.cO(), 1.0F, 1.0F);
                                                this.fn();
                                                break label179;
                                            }

                                            entityliving = (EntityLiving)iterator.next();
                                        } while(entityliving == this);
                                    } while(entityliving == entity);
                                } while(this.p(entityliving));
                            } while(entityliving instanceof EntityArmorStand && ((EntityArmorStand)entityliving).t());

                            if (this.f(entityliving) < 9.0D && entityliving.a(DamageSource.a(this).sweep(), f4)) {
                                entityliving.p(0.4000000059604645D, (double)MathHelper.a(this.dn() * 0.017453292F), (double)(-MathHelper.b(this.dn() * 0.017453292F)));
                            }
                        }
                    }

                    if (entity instanceof EntityPlayer && entity.D) {
                        boolean cancelled = false;
                        Player player = (Player)entity.getBukkitEntity();
                        Vector velocity = CraftVector.toBukkit(vec3d);
                        PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                        this.s.getCraftServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            cancelled = true;
                        } else if (!velocity.equals(event.getVelocity())) {
                            player.setVelocity(event.getVelocity());
                        }

                        if (!cancelled) {
                            ((EntityPlayer)entity).b.a(new PacketPlayOutEntityVelocity(entity));
                            entity.D = false;
                            entity.g(vec3d);
                        }
                    }

                    if (flag2) {
                        this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.ow, this.cO(), 1.0F, 1.0F);
                        this.a(entity);
                    }

                    if (!flag2 && !flag3) {
                        if (flag) {
                            this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.oz, this.cO(), 1.0F, 1.0F);
                        } else {
                            this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.oB, this.cO(), 1.0F, 1.0F);
                        }
                    }

                    if (f1 > 0.0F) {
                        this.b(entity);
                    }

                    this.x(entity);
                    if (entity instanceof EntityLiving) {
                        EnchantmentManager.a((EntityLiving)entity, this);
                    }

                    EnchantmentManager.b(this, entity);
                    ItemStack itemstack1 = this.es();
                    Object object = entity;
                    if (entity instanceof EntityComplexPart) {
                        object = ((EntityComplexPart)entity).b;
                    }

                    if (!this.s.x && !itemstack1.b() && object instanceof EntityLiving) {
                        itemstack1.a((EntityLiving)object, this);
                        if (itemstack1.b()) {
                            this.a((EnumHand)EnumHand.a, (ItemStack)ItemStack.b);
                        }
                    }

                    if (entity instanceof EntityLiving) {
                        float f5 = f3 - ((EntityLiving)entity).ea();
                        this.a(StatisticList.G, Math.round(f5 * 10.0F));
                        if (j > 0) {
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), j * 4);
                            Bukkit.getPluginManager().callEvent(combustEvent);
                            if (!combustEvent.isCancelled()) {
                                entity.setSecondsOnFire(combustEvent.getDuration(), false);
                            }
                        }

                        if (this.s instanceof WorldServer && f5 > 2.0F) {
                            int k = (int)((double)f5 * 0.5D);
                            ((WorldServer)this.s).a(Particles.h, entity.dc(), entity.e(0.5D), entity.di(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                        }
                    }

                    this.causeFoodExhaustion(this.s.spigotConfig.combatExhaustion, ExhaustionReason.ATTACK);
                } else {
                    this.s.a((EntityHuman)null, this.dc(), this.de(), this.di(), SoundEffects.oy, this.cO(), 1.0F, 1.0F);
                    if (flag4) {
                        entity.au();
                    }

                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer)this).getBukkitEntity().updateInventory();
                    }
                }
            }
        }

    }
