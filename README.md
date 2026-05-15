# Grama-Urja ⚡

A small app I built to solve a real problem in my village area — farmers wasting hours just to check if the power is back so they can run their irrigation pumps. Instead of waiting for an official update, anyone in the same zone can tap ON/OFF and everyone else sees it instantly.

Basically a "human-powered smart grid" for rural electricity.

---

## Why I built this

Power cuts in rural areas are super unpredictable. I've seen people drive 4-5 km just to check the transformer, find out power is still off, and drive back. Multiply that across a village and it's a huge waste of time, fuel, and water (because by the time someone realises power is back, the irrigation window is gone).

There's no official real-time feed from the electricity board, so the next best thing is letting the community itself report it. That's what this app does.

---

## What it does

- **One-tap power status** — ON / OFF toggle for your zone
- **Real-time sync** — anyone in the same zone sees the update within ~2 seconds
- **Freshness label** — "Updated 3 mins ago" so you know if the info is stale
- **Pump timer** — pick your crop, get a recommended irrigation duration
- **Zone-based** — updates only affect the zone you belong to (no random people changing your village's status)
- **Multi-language** — English, Hindi, Kannada (most of the farmers I tested with prefer Kannada)
- **Admin panel** — for managing zones and users
- **Login / Signup** — proper auth, not anonymous, so spam updates can be traced

---

## Tech I used

I went with a web-first approach so it works on any phone without needing Play Store install, and then also did a native Android port.

**Web app (main one):**
- React 19 + TanStack Start (file-based routing, SSR ready)
- TypeScript
- Tailwind CSS + shadcn/ui components
- Lucide icons
- Service Worker + Web Push API for notifications
- Supabase for database + auth + realtime (I started with Firebase like in my SRS but switched to Supabase mid-way because PostgreSQL + RLS gave me much cleaner row-level security than Firebase rules — wrote a note about this below)

**Android version (in `/android` folder):**
- Kotlin + Jetpack Compose
- Material 3
- Supabase Kotlin SDK
- Foreground Service for background notifications

---

## Architecture

![Architecture](./docs/architecture.png)

The flow is pretty simple:

1. Farmer opens app → logs in → lands on home screen showing their zone's current power status
2. Taps ON or OFF → write goes to Supabase `zones` table
3. Supabase Realtime broadcasts the change to every other client subscribed to that zone
4. Service Worker on each phone fires an OS-level notification ("Power is back in Zone 3 ⚡")

![Realtime sequence](./docs/sequence_realtime.png)

---

## Database

![ER Diagram](./docs/er_diagram.png)

Three main tables:

**`profiles`** — extends auth.users
- id, display_name, zone_id, language, phone

**`zones`**
- id, name, description, power_status (ON/OFF), last_updated_at, last_updated_by

**`user_roles`** — kept separate from profiles on purpose
- id, user_id, role (admin / farmer)

Roles are in their own table because storing roles on the profile/user row is a known privilege-escalation footgun. Access is checked via a `SECURITY DEFINER` function `has_role(user_id, role)` so RLS policies don't recurse on themselves.

---

## Screens

| Route | What it does |
|---|---|
| `/` | Landing page |
| `/auth` | Login / Signup |
| `/app` | Home — current power status + last updated |
| `/app/zones` | List of zones (admin can add/delete) |
| `/app/pump` | Pump timer based on crop type |
| `/app/settings` | Language switcher, profile |
| `/app/admin` | Admin-only panel (only visible if user has `admin` role) |

---

## Note on Firebase vs Supabase

My SRS originally listed Firebase Realtime Database + FCM. I started with that but ran into two issues:

1. Firebase rules get messy fast when you need role-based + zone-based access at the same time
2. I wanted proper SQL relations between zones, users, and roles — Firebase's NoSQL model made joins awkward

So I migrated to **Supabase** (PostgreSQL + Realtime + Auth + Row Level Security). For notifications I used the **Web Push API** with a Service Worker instead of FCM on the web side, and kept FCM-style notifications via a Foreground Service in the Android port. Functionally identical to what the SRS describes, just a cleaner stack.

---

## Admin account

For the demo / evaluation:

- **Email:** `sharmarithika21@gmail.com`  
- **Role:** admin (set in `user_roles` table)

This account can:
- See the Admin tab in the bottom nav
- Add and delete zones
- Do everything a normal farmer can do

To make any other user an admin, run this in the database:

```sql
INSERT INTO user_roles (user_id, role)
VALUES ('<that-user-uuid>', 'admin');
```

---

## Running it locally

You'll need Node.js 20+ and bun (or npm).

```bash
bun install
bun run dev
```

The `.env` is auto-managed by Lovable Cloud (Supabase URL + anon key get injected). For a production build:

```bash
bun run build
bun run start
```

For the Android version, open the `/android` folder in Android Studio, let Gradle sync (8.7, JDK 17, SDK 34), then hit Run.

---

## About notifications

Notifications work in two cases:
- **Published web app installed via "Add to Home Screen"** on Android Chrome
- **Desktop browsers** (Chrome, Edge, Firefox)

---

## Things I'd add later if I had more time

- Historical charts (how many hours of power per day this week?)
- SMS fallback for users without internet
- Smart-meter integration (if EB ever opens an API)
- A "reporter" role — trusted users whose updates count more
- Community chat per zone
- Solar irrigation tracking

These were in my "out of scope" list in the SRS so I didn't build them, but the database is structured to add them without rewrites.

---

## Folder structure

```
.
├── src/
│   ├── routes/          # all pages (file-based routing)
│   ├── components/      # AppHeader, BottomNav, FreshnessLabel, shadcn ui
│   ├── lib/             # auth-context, i18n, register-sw
│   └── integrations/    # supabase client (auto-generated, don't edit)
├── public/
│   ├── sw.js            # service worker for push notifications
│   └── manifest.webmanifest
├── android/             # native Kotlin port (Jetpack Compose)
└── docs/                # diagrams + screenshots
```

---

## Credits

Built as part of my MindMatrix internship project. 
-Rithika
