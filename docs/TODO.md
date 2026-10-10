# Postmark — Active TODOs
Last updated: October 9, 2026
Ordered by priority tier. Work top-to-bottom within each tier.

**Open work only.** Finished work goes in `docs/CHANGELOG.md`: when you finish an item,
write its changelog entry and delete it from this file. Don't tick it and leave it here.

---

## 🔴 TIER 1 — Core Loop (app unusable as daily driver without these)

### Reactions
- [ ] **Reaction parsing — on-device verification** — run the checklist in
      `docs/fable-reaction-parsing.md`. After one catch-up: the Fry thread's empty
      bubble heals into a ❤️ on the image (or logs `EmptyMmsRepair: still empty`);
      the Tonya thread's raw `❤️ to "https://…"` bubble resolves; the stray bare ❤️
      becomes a pill on the cat photo; the stray `Removed 👍 from …` bubble disappears
      and its 👍 pill clears. A fresh media reaction attaches, a fresh live un-react
      clears without leaving a bubble, and a lone emoji typed as a real SMS stays a
      normal message.
      **Still-unknown shape:** the removal form for a **bare-emoji** media reaction.
      Capture an unreact on a media message over RCS via SyncLogger before building it.

---

## 🟡 TIER 2 — Feature Complete (needed before Play Store submission)

### Contact integration
- [ ] **Contact-level settings across a contact's numbers** — someone with two
      numbers gets two independent threads, each with its own mute / notifications /
      nickname / color / pin state. Needs a product decision on whether (and how)
      those share one identity.

### Performance & optimization
> Mirrors the open items in `docs/performance-analysis.md`; keep the two in step.
> Audited 2026-09-23: none of these have been started.
- [ ] **Thread view initial load** (Tier 1 #3) — profile cold-open of a 1000+ message
      thread; if first frame is > 16 ms, page it (windowed query or Paging3).
- [ ] **Baseline profile module** (Tier 1 #4) — no `:baselineprofile` module exists.
- [ ] **Coalesce sync writes into one transaction** (Tier 2 #6).
- [ ] **Reaction resolution via a window query** (Tier 2 #9) instead of per-message lookups.
- [ ] **Drain `SyncLogger` asynchronously** (Tier 2 #11) — currently writes inline.
- [ ] **`SubcomposeAsyncImage` → `AsyncImage`** (Tier 2 #12) and **fixed-size image
      placeholders** (#13).
- [ ] **Migrate to `coil-video`** (Tier 2 #14) for video thumbnails (coil pinned at 2.7.0).
- [ ] **Compose compiler metrics reports** (Tier 4 #23) to find unstable parameters.
- [ ] **Bump the Compose BOM** (Tier 4 #26) — still `2025.01.00`.
- [ ] **Trim `material-icons-extended`** (Tier 4 #27).
- [ ] **Raise the Gradle heap** (Tier 4 #28).

### Backup
- [ ] **Readable export: `index.html` with inline thumbnails** — optional follow-up to
      the "Readable text + media" export.

---

## 🟢 TIER 3 — Polish and Depth

### Thread view
- [ ] **Scrolling screenshot ("Capture more") in threads** — re-fixed 2026-07-24
      (`fix/thread-scrollcapture-selection`, see CHANGELOG) after the first build fell
      back to Samsung's "isn't supported" flat capture. **Not yet device-verified**
      (API 31+): screenshot a long thread, confirm "Capture more" appears with no
      "isn't supported" toast, the stitched image has no gaps/overlaps/repeated
      bars, scroll position is restored, the conversations list's long-screenshot
      still works, and normal scrolling/long-press is unaffected. If OneUI won't pick
      the occluded child View, next try: overlay in front with pass-through touches, or
      `SCROLL_CAPTURE_HINT_EXCLUDE` on the ComposeView.
- [ ] **Outbound reactions — media targets** — reacting to a photo/video/voice memo
      still stays local-only. Confirm Google Messages' real media-fallback wording
      on-device first (e.g. `😎 to "📷 Photo"`), then extend `composeReactionFallback`
      for blank-body targets behind the existing round-trip gate.
- [ ] **Outbound reactions — group threads** — needs a group MMS to the full roster
      (mirroring `sendMessage`'s MMS branch) plus end-to-end checks of inbound
      group-reaction attribution. Decide whether the transient fallback bubble is
      acceptable in a busy group first.
- [ ] **Read receipt double tick** — ✓✓ in accent color when an MMS read report
      arrives. Blocked: no MMS/RCS read-report source exists, so there is nothing
      to drive it (which is also why `readAt` was never added).
- [ ] **Link detection for street addresses** → open in Maps (URLs and phone numbers
      already linkify).
- [ ] **Avatars per bubble in group threads** — optional follow-up to sender-name labels.
- [ ] **Flagged messages: global cross-thread list** — reminders are per-thread only;
      needs a cross-thread `observeFlagged()` plus a Settings surface.
- [ ] **Re-arm restored reminders** — backup/restore keeps `remindAt` but doesn't
      reschedule the WorkManager job for a future reminder.
- [ ] **Scheduled sends: backup/restore and MMS** — v1 is text-only SMS and pending
      sends aren't carried through backup.
- [ ] **Scheduled sends: exact timing?** — open owner question: WorkManager fires
      ± a few minutes. If punctuality matters, swap in `SCHEDULE_EXACT_ALARM` at the
      single `ScheduledSendWorker.schedule()` call site.
- [ ] **Forwarded messages: live delivery status** — a forward's optimistic row has no
      PendingIntent, so status only updates on the next sync. Revisit if it feels laggy.

### Window insets
- [ ] **Device-check the two SearchScreen filter sheets** — they rely on M3
      `ModalBottomSheet`'s default `contentWindowInsets` alone, while
      `EmojiPickerBottomSheet` adds `navigationBarsPadding()` explicitly. If either
      clips on-device, it's a one-line fix.

### Message retention & auto-cleanup
- [ ] **Auto-cleanup setting** — new section in Settings alongside Backup. Threshold:
      1 month / 3 months / 6 months / 1 year / custom / never (default). WorkManager
      periodic job.
- [ ] **Scope modes** — Global (one threshold for all threads); Per-thread override
      (set from the thread ⋮ menu); Exclusionary (global threshold except an explicit
      exclusion list in Settings).
- [ ] **Preview before delete** — "X messages across Y threads will be deleted", with
      review or cancel. Suppressible via "Don't ask again".
- [ ] **Locked messages** — `isLocked` on `MessageEntity`; long-press → Lock; skipped
      by auto-cleanup, bulk delete and every non-explicit delete; 🔒 on the bubble.
- [ ] **Pinned + locked exemption in the cleanup query** — filter out `isPinned = 1`
      OR `isLocked = 1` before deleting. (Message pinning already exists.)
- [ ] **Cleanup log** — last run time, threads affected, messages deleted; shown in
      the Backup status area.

### Privacy (Settings → Privacy)
- [ ] **Strip image metadata before sending** (owner request, July 24 2026) — toggle,
      default ON. Over-budget images already lose EXIF when `compressImage`
      re-encodes them; under-budget images go out byte-for-byte with GPS/device/time
      intact. Fix: when the toggle is on, route every outgoing image through the same
      decode → bake rotation → re-encode path (pure `shouldReencode(toggle,
      overBudget, mimeType)`). Subtitle notes PNG/GIF lose format when re-encoded.
- [ ] **Strip video metadata before sending** — under-budget videos keep location in
      the MP4 `moov/udta` box. Needs a MediaMuxer remux pass (or re-encode small
      videos too). Ship the image toggle first, labelled "photos" until this lands.
- [ ] **App lock** — `BiometricPrompt` with device-credential fallback; timeout
      options (immediately / 1 min / 5 min). Say plainly that notifications still show
      content unless notification privacy mode is on; consider suggesting it.
- [ ] **Locked conversations** — thread ⋮ → "Lock conversation"; name-only rows,
      redacted notifications (reuse privacy mode's path per thread), auth to open.
      Depends on App lock. Schema: one nullable flag on `ThreadEntity`.
- [ ] **Block screenshots (screen security)** — `FLAG_SECURE` toggle; also blanks the
      app in recents, so word it to cover both. Check `ThreadScrollCapture` still
      works with the flag set, or gate it.
- [ ] **Incognito keyboard in the composer** — `IME_FLAG_NO_PERSONALIZED_LEARNING` on
      the reply field. Advisory only; subtitle says "supported keyboards".
- [ ] **Per-thread notification privacy** — the global privacy mode as a per-thread
      override, in the thread ⋮ menu. Nearly free once Locked conversations lands.

### Settings
- [ ] **Per-conversation notification channels** — real per-thread sound/vibration
      needs one channel per thread (`sms_thread_<threadId>`, cloned from
      `incoming_sms`, titled with the contact name) and a thread ⋮ entry via
      `ACTION_CHANNEL_NOTIFICATION_SETTINGS`. Costs: orphaned channels persist after
      thread deletion and can't be silently recreated, a threadId → channelId mapping,
      a migration for existing threads, and interaction with the group-summary
      channel. Standalone design effort.
- [ ] **Real app icon** — an adaptive icon already exists
      (`mipmap-anydpi-v26/ic_launcher.xml`); whether it counts as proper branded
      artwork is the owner's call. Gated on the trademark question below.
- [ ] **Per-thread appearance override** — font family, text size and bubble styling
      per conversation, via thread ⋮ → "Customize appearance". Nullable override
      columns on `ThreadEntity` (null = global default); `ThreadScreen` reads the
      override ahead of the global preference.
- [ ] **Per-contact row styling in the conversation list** — opt-in background color
      or image banner on a thread's home-list row. Build in the same `ThreadEntity`
      schema pass as the appearance override. Companion setting: list rows follow the
      app font + theme color by default, with an opt-out in Settings → Appearance.

### Personalization — remaining
> Spec and file format: `docs/theme-presets-plan.md`. P1–P2 shipped 2026-07-18.
- [ ] **Theme preset share / import (P3)** — export a thread's look as a
      `.postmarktheme` file via the share sheet; import via file-open → preview →
      apply or save. Needs a "My presets" store. The codec already round-trips.
- [ ] **Global apply (P4)** — blocked on deciding what "apply to everything" means;
      there's no global default bubble-pair preference, only the app accent.
- [ ] **Community gallery (P5)** — a static web home for `.postmarktheme` files.
      No app changes beyond P3.
- [ ] **Visual pass over every preset** in light + dark, both bubble styles, with
      screenshots — needs an emulator or device.

---

## 🔵 TIER 4 — Infrastructure / Housekeeping

### Build and schema
- [ ] **`NOT NULL DEFAULT` schema drift on `messages`** (surfaced 2026-09-21).
      Migrations add `isStarred`, `isPinned`, `isRead`, `isMms`, `deliveryStatus` and
      others as `NOT NULL DEFAULT 0`, but the entities declare no
      `@ColumnInfo(defaultValue = …)`, so upgraded installs have the default and
      fresh installs don't. Harmless at runtime (Room always writes the column), and
      Room's validation doesn't compare defaults. Options: (a) leave it and keep
      migration-test INSERTs exhaustive, as now; (b) add `defaultValue` to the
      entities, which regenerates the current schema JSON and changes what
      validation accepts. Needs a deliberate call, not a blind fix.
- [ ] **AGP 10 deprecation cleanup, part 2 — `newDsl` / `builtInKotlin`**. The
      obsolete-API warning comes from KGP 2.2.10 itself (traced with
      `-Pandroid.debug.obsoleteApi=true`), not Firebase. Dropping either flag alone
      breaks the build against this KGP: `builtInKotlin` → `Cannot add extension
      with name 'kotlin'`; `newDsl` → `…cannot be cast to … BaseExtension`. Fix
      when a KGP release supports AGP's new DSL / built-in Kotlin: drop the
      `org.jetbrains.kotlin.android` + `kotlin.plugin.compose` plugins for AGP's
      built-in Kotlin (https://developer.android.com/r/tools/built-in-kotlin).
      Touches all plugin wiring in `app/build.gradle.kts`, so don't do it piecemeal.
      Check KGP release notes whenever bumping Kotlin.
- [ ] **Instrumented CI cost** — `instrumented.yml` boots an emulator on every push to
      master (~15–25 runner minutes). Keep it, or narrow the trigger (commit-message
      tag or a schedule). Flag any new red run here.

### Accessibility
- [ ] **RTL layout support** — mirror layout for Arabic/Hebrew; test with the device
      set to Arabic.

### Owner decisions (not code — see `docs/OWNER-ACTIONS.md` for the full writeup)
> Re-verified 2026-09-23: all three still open.
- [ ] **Backup encryption** — backups and exports are plaintext zips. The v2 manifest
      already reserves an `encryption` field. Recommendation: optional, opt-in,
      passphrase-derived AES-GCM. Needs a go/no-go.
- [ ] **Release-signed tester builds** — testers install a build signed with the
      publicly committed `debug.keystore`; there's no `signingConfigs.release`.
      Needs a keystore plus repo secrets.
- [ ] **"Postmark" trademark collision** — unresolved against the Postmark
      email-delivery service. Blocks further branding and icon work.

### Play Store prep (when ready)
- [ ] **Privacy policy** — required for SMS permissions; host at a public URL. Draft:
      `docs/PRIVACY_POLICY_DRAFT.md` (needs owner review + hosting).
- [ ] **App description copy** — short + long description, 8 screenshots, feature
      graphic. Draft: `docs/PLAY_LISTING_DRAFT.md` (needs owner review + assets).
- [ ] **Content rating questionnaire** — messaging apps answer questions about
      user-generated content.
- [ ] **Target SDK gaps** — audit in `docs/TARGET_SDK_REVIEW.md` found two:
      **G1** Android 15's 6h/24h dataSync FGS timeout on the bulk importer — verify
      on a large mailbox. **G2** 16 KB page-size alignment of media3 1.5.1's native
      `.so` files — Play-blocking (the deadline passed 2026-05-31); check the built
      AAB, may need a media3 bump.
- [ ] **Samsung Galaxy Store** — consider dual submission; Samsung users are the
      primary target.

---

## 📱 On-device verification backlog

Shipped and unit-tested, but never exercised on a real phone. Each changelog entry
has the full checklist; the date and branch here are the lookup key. Delete a line
once it's checked on a device (and fix anything it turns up).

**Thread view**
- Date pill fades out when selection mode starts mid-scroll — 07-23 `fix/date-pill-selection-overlap`
- Outbound reactions: shows as a reaction on Google Messages, un-react clears, offline → Queued, media/group stay local — 07-23 `feat/outbound-reactions`
- Timestamp chip over photo backgrounds; reactions + timestamp share one row — 07-23 `fix/timestamp-legibility`
- Paged date-range picker — 09-20 `feat/paged-date-range-picker`
- Scroll-capture — see Tier 3 above
- Search-jump lands centered with the pop cue, from all three entry points — 07-24 `fix/search-jump-arrival-cue`
- Reaction pills take the sent/received bubble colors — 07-24 `fix/reaction-pill-sender-colors`
- Message info sheet: insets, 5-action row at large font, Delivered row — 07-24 `feat/delivery-timestamps`
- Reminders: fire on time, deep-link, survive reboot, unflag cancels, 6-action row fits — 07-24 `feat/message-reminders`
- Schedule send: survives restart, fires, Send now / Edit / Cancel, offline → Queued, role-revoked notification — 07-24 `feat/scheduled-messages`
- Pinned messages sheet UX; v19 migration against real data — 07-22
- Large text: bubbles at pinch 1.6× + max font, avatar letters, top-bar ellipsis, onboarding scroll — 07-23 `fix/dynamic-text-reflow`

**Notifications & sending**
- Conversation-style notifications land in the Conversations section from message two — 07-22
- No notification for the thread currently open — 07-22
- Multipart send: all-parts-OK → Sent, induced failure stays Failed — 07-23 `fix/multipart-sent-status`
- Send queue: airplane-mode send → Queued → flushes in order — 07-23 `feat/send-queue`
- Notification "Mark as read" on group MMS sticks after sync — 07-23 `fix/markread-mms`
- Mute/spam suppression from an alternate-format number; picker number-type labels — 07-23 `fix/multi-number-contacts`

**MMS & media**
- Group messaging matrix (`docs/GROUP_MESSAGING_SPEC.md` §5), incl. the demoted misclassified 1:1 thread — 07-19
- Camera capture → MMS send, cancel cleanup, process death while camera is open — 07-24 `feat/camera-capture`
- Voice memos: record → preview → send, lock mode, cancel, permission denial, cap auto-stop, no bar jump with keyboard open — 07-16/17

**Contacts, list & search**
- "Add to contacts?" banner and post-add disappearance — 07-23 `feat/save-number-prompt`
- Contact profile Photos/Videos/Links sections and full grids — 07-24 `feat/contact-media-gallery`
- Swipe actions: 40% threshold feel, no scroll conflict, reveal colors — 07-23 `feat/conversation-swipe-actions`
- Bulk Report spam / Block, group-skip snackbar, default-SMS gate — 07-24 `feat/bulk-spam-block`
- Block number / Blocked numbers screen (`BlockedNumberContract` is device-dependent) — 07-11, 07-22
- Spam folder; "Looks like spam?" banner; notification Report-spam action — 07-22, 07-23 `feat/spam-heuristics`
- Search group collapse / expand-all; chip row at large font — 07-24 `feat/search-group-collapse`

**Settings, backup & stats**
- Backup restore, selective export, readable export — 07-11, 07-12
- Storage usage sizes plausible; both cleanup actions free what they report — 07-23 `feat/storage-usage`
- About: licenses list renders and links open; bottom row clears the nav bar — 07-24 `feat/about-licenses`
- Stats doughnut, emoji bars, "Gone quiet" card — 07-23 `feat/stats-followups`
- Stats month switch / open at 150k rows — 07-23 `perf/stats-sql-aggregation`
