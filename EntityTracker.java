//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.slf4j.Logger;

public class EntityTrackerEntry {
    private static final Logger a = LogUtils.getLogger();
    private static final int b = 1;
    private final WorldServer c;
    private final Entity d;
    private final int e;
    private final boolean f;
    private final Consumer<Packet<?>> g;
    private long h;
    private long i;
    private long j;
    private int k;
    private int l;
    private int m;
    private Vec3D n;
    private int o;
    private int p;
    private List<Entity> q;
    private boolean r;
    private boolean s;
    private final Set<ServerPlayerConnection> trackedPlayers;

    public EntityTrackerEntry(WorldServer worldserver, Entity entity, int i, boolean flag, Consumer<Packet<?>> consumer, Set<ServerPlayerConnection> trackedPlayers) {
        this.trackedPlayers = trackedPlayers;
        this.n = Vec3D.a;
        this.q = Collections.emptyList();
        this.c = worldserver;
        this.g = consumer;
        this.d = entity;
        this.e = i;
        this.f = flag;
        this.d();
        this.k = MathHelper.d(entity.dn() * 256.0F / 360.0F);
        this.l = MathHelper.d(entity.do() * 256.0F / 360.0F);
        this.m = MathHelper.d(entity.ce() * 256.0F / 360.0F);
        this.s = entity.aw();
    }

    public void a() {
        List<Entity> list = this.d.cF();
        if (!list.equals(this.q)) {
            this.q = list;
            this.a((Packet)(new PacketPlayOutMount(this.d)));
        }

        if (this.d instanceof EntityItemFrame) {
            EntityItemFrame entityitemframe = (EntityItemFrame)this.d;
            ItemStack itemstack = entityitemframe.x();
            if (this.o % 10 == 0 && itemstack.c() instanceof ItemWorldMap) {
                Integer integer = ItemWorldMap.d(itemstack);
                WorldMap worldmap = ItemWorldMap.a(integer, this.c);
                if (worldmap != null) {
                    Iterator iterator = this.trackedPlayers.iterator();

                    while(iterator.hasNext()) {
                        EntityPlayer entityplayer = ((ServerPlayerConnection)iterator.next()).e();
                        worldmap.a(entityplayer, itemstack);
                        Packet<?> packet = worldmap.a(integer, entityplayer);
                        if (packet != null) {
                            entityplayer.b.a(packet);
                        }
                    }
                }
            }

            this.c();
        }

        if (this.o % this.e == 0 || this.d.af || this.d.ai().a()) {
            int i;
            int j;
            if (this.d.bF()) {
                i = MathHelper.d(this.d.dn() * 256.0F / 360.0F);
                j = MathHelper.d(this.d.do() * 256.0F / 360.0F);
                boolean flag = Math.abs(i - this.k) >= 1 || Math.abs(j - this.l) >= 1;
                if (flag) {
                    this.g.accept(new PacketPlayOutEntityLook(this.d.ae(), (byte)i, (byte)j, this.d.aw()));
                    this.k = i;
                    this.l = j;
                }

                this.d();
                this.c();
                this.r = true;
            } else {
                ++this.p;
                i = MathHelper.d(this.d.dn() * 256.0F / 360.0F);
                j = MathHelper.d(this.d.do() * 256.0F / 360.0F);
                Vec3D vec3d = this.d.cV().d(PacketPlayOutEntity.a(this.h, this.i, this.j));
                boolean flag1 = vec3d.g() >= 7.62939453125E-6D;
                Packet<?> packet1 = null;
                boolean flag2 = flag1 || this.o % 60 == 0;
                boolean flag3 = Math.abs(i - this.k) >= 1 || Math.abs(j - this.l) >= 1;
                if (flag2) {
                    this.d();
                }

                if (flag3) {
                    this.k = i;
                    this.l = j;
                }

                if (this.o > 0 || this.d instanceof EntityArrow) {
                    long k = PacketPlayOutEntity.a(vec3d.b);
                    long l = PacketPlayOutEntity.a(vec3d.c);
                    long i1 = PacketPlayOutEntity.a(vec3d.d);
                    boolean flag4 = k < -32768L || k > 32767L || l < -32768L || l > 32767L || i1 < -32768L || i1 > 32767L;
                    if (!flag4 && this.p <= 400 && !this.r && this.s == this.d.aw()) {
                        if ((!flag2 || !flag3) && !(this.d instanceof EntityArrow)) {
                            if (flag2) {
                                packet1 = new PacketPlayOutRelEntityMove(this.d.ae(), (short)((int)k), (short)((int)l), (short)((int)i1), this.d.aw());
                            } else if (flag3) {
                                packet1 = new PacketPlayOutEntityLook(this.d.ae(), (byte)i, (byte)j, this.d.aw());
                            }
                        } else {
                            packet1 = new PacketPlayOutRelEntityMoveLook(this.d.ae(), (short)((int)k), (short)((int)l), (short)((int)i1), (byte)i, (byte)j, this.d.aw());
                        }
                    } else {
                        this.s = this.d.aw();
                        this.p = 0;
                        packet1 = new PacketPlayOutEntityTeleport(this.d);
                    }
                }

                if ((this.f || this.d.af || this.d instanceof EntityLiving && ((EntityLiving)this.d).eV()) && this.o > 0) {
                    Vec3D vec3d1 = this.d.da();
                    double d0 = vec3d1.g(this.n);
                    if (d0 > 1.0E-7D || d0 > 0.0D && vec3d1.g() == 0.0D) {
                        this.n = vec3d1;
                        this.g.accept(new PacketPlayOutEntityVelocity(this.d.ae(), this.n));
                    }
                }

                if (packet1 != null) {
                    this.g.accept(packet1);
                }

                this.c();
                this.r = false;
            }

            i = MathHelper.d(this.d.ce() * 256.0F / 360.0F);
            if (Math.abs(i - this.m) >= 1) {
                this.g.accept(new PacketPlayOutEntityHeadRotation(this.d, (byte)i));
                this.m = i;
            }

            this.d.af = false;
        }

        ++this.o;
        if (this.d.D) {
            boolean cancelled = false;
            if (this.d instanceof EntityPlayer) {
                Player player = (Player)this.d.getBukkitEntity();
                Vector velocity = player.getVelocity();
                PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                this.d.s.getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    player.setVelocity(event.getVelocity());
                }
            }

            if (!cancelled) {
                this.a((Packet)(new PacketPlayOutEntityVelocity(this.d)));
            }

            this.d.D = false;
        }

    }

    public void a(EntityPlayer entityplayer) {
        this.d.d(entityplayer);
        entityplayer.b.a(new PacketPlayOutEntityDestroy(new int[]{this.d.ae()}));
    }

    public void b(EntityPlayer entityplayer) {
        PlayerConnection playerconnection = entityplayer.b;
        Objects.requireNonNull(entityplayer.b);
        this.sendPairingData(playerconnection::a, entityplayer);
        this.d.c(entityplayer);
    }

    public void sendPairingData(Consumer<Packet<?>> consumer, EntityPlayer entityplayer) {
        if (!this.d.dp()) {
            Packet<?> packet = this.d.S();
            this.m = MathHelper.d(this.d.ce() * 256.0F / 360.0F);
            consumer.accept(packet);
            if (!this.d.ai().d()) {
                consumer.accept(new PacketPlayOutEntityMetadata(this.d.ae(), this.d.ai(), true));
            }

            boolean flag = this.f;
            if (this.d instanceof EntityLiving) {
                Collection<AttributeModifiable> collection = ((EntityLiving)this.d).eq().b();
                if (this.d.ae() == entityplayer.ae()) {
                    ((EntityPlayer)this.d).getBukkitEntity().injectScaledMaxHealth(collection, false);
                }

                if (!collection.isEmpty()) {
                    consumer.accept(new PacketPlayOutUpdateAttributes(this.d.ae(), collection));
                }

                if (((EntityLiving)this.d).eV()) {
                    flag = true;
                }
            }

            this.n = this.d.da();
            if (flag && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
                consumer.accept(new PacketPlayOutEntityVelocity(this.d.ae(), this.n));
            }

            if (this.d instanceof EntityLiving) {
                List<Pair<EnumItemSlot, ItemStack>> list = Lists.newArrayList();
                EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
                int i = aenumitemslot.length;

                for(int j = 0; j < i; ++j) {
                    EnumItemSlot enumitemslot = aenumitemslot[j];
                    ItemStack itemstack = ((EntityLiving)this.d).b(enumitemslot);
                    if (!itemstack.b()) {
                        list.add(Pair.of(enumitemslot, itemstack.n()));
                    }
                }

                if (!list.isEmpty()) {
                    consumer.accept(new PacketPlayOutEntityEquipment(this.d.ae(), list));
                }

                ((EntityLiving)this.d).x();
            }

            this.m = MathHelper.d(this.d.ce() * 256.0F / 360.0F);
            consumer.accept(new PacketPlayOutEntityHeadRotation(this.d, (byte)this.m));
            if (this.d instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving)this.d;
                Iterator iterator = entityliving.dX().iterator();

                while(iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect)iterator.next();
                    consumer.accept(new PacketPlayOutEntityEffect(this.d.ae(), mobeffect));
                }
            }

            if (!this.d.cF().isEmpty()) {
                consumer.accept(new PacketPlayOutMount(this.d));
            }

            if (this.d.bF()) {
                consumer.accept(new PacketPlayOutMount(this.d.cN()));
            }

            if (this.d instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient)this.d;
                if (entityinsentient.fq()) {
                    consumer.accept(new PacketPlayOutAttachEntity(entityinsentient, entityinsentient.fr()));
                }
            }

        }
    }

    private void c() {
        DataWatcher datawatcher = this.d.ai();
        if (datawatcher.a()) {
            this.a((Packet)(new PacketPlayOutEntityMetadata(this.d.ae(), datawatcher, false)));
        }

        if (this.d instanceof EntityLiving) {
            Set<AttributeModifiable> set = ((EntityLiving)this.d).eq().a();
            if (!set.isEmpty()) {
                if (this.d instanceof EntityPlayer) {
                    ((EntityPlayer)this.d).getBukkitEntity().injectScaledMaxHealth(set, false);
                }

                this.a((Packet)(new PacketPlayOutUpdateAttributes(this.d.ae(), set)));
            }

            set.clear();
        }

    }

    private void d() {
        this.h = PacketPlayOutEntity.a(this.d.dc());
        this.i = PacketPlayOutEntity.a(this.d.de());
        this.j = PacketPlayOutEntity.a(this.d.di());
    }

    public Vec3D b() {
        return PacketPlayOutEntity.a(this.h, this.i, this.j);
    }

    private void a(Packet<?> packet) {
        this.g.accept(packet);
        if (this.d instanceof EntityPlayer) {
            ((EntityPlayer)this.d).b.a(packet);
        }

    }
}
