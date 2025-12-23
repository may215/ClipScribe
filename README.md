# 🎧 ClipScribe (Android)

**ClipScribe** היא אפליקציית Android בקוד פתוח שמאפשרת ליצור **קליפים קצרים מתוך סרטוני YouTube** (על בסיס timestamp), לערוך את טווח הקליפ, ולהציג **תמלול מסונכרן בסגנון אפליקציית אודיו** — עם timestamps, הדגשה בזמן ניגון, גלילה אוטומטית וייצוא.

האפליקציה תוכננה להיות:
- מהירה לשימוש (Share → תוצאה)
- קריאה ונוחה (RTL מלא, עברית-first)
- בטוחה וחוקית (ללא הורדת מדיה מיוטיוב)

---

## 🎯 מוטיבציה

הרבה פעמים אנחנו רוצים:
- לשמור **קטע קצר** מתוך הרצאה, שיעור, פודקאסט או ראיון ביוטיוב
- לחזור לקטע מסוים שוב ושוב
- להוציא ממנו **טקסט מסודר** (סיכום, ציטוט, לימוד, תיעוד)

אבל:
- YouTube לא בנוי לעבודה עם קליפים קצרים אישיים
- תמלולים לא תמיד נוחים לקריאה
- אין חוויית “אודיו + טקסט מסונכרן” לקליפים קצרים

**ClipScribe נוצרה כדי לפתור את זה.**

---

## ✨ מה האפליקציה עושה

1. 📤 משתפים קישור YouTube (כולל timestamp)
2. 📍 האפליקציה נפתחת ישירות על הקטע הרלוונטי
3. ⏱️ נוצר קליפ ברירת מחדל (30s / 1m / 2m / 5m)
4. ✂️ ניתן לערוך Start / End עם Slider
5. ▶️ ניגון מתחיל ב-Start ועוצר אוטומטית ב-End
6. 📝 תמלול מוצג עם timestamps + highlight + auto-scroll
7. 💾 שמירה לספרייה מקומית
8. 📄 ייצוא התמלול (Markdown / PDF)

---

## 🚀 פיצ’רים עיקריים

- Share & Open מ-YouTube
- עריכת קליפ עם RangeSlider
- Toggle אורך ברירת מחדל
- נגן YouTube משולב
- תמלול עם timestamps + highlight
- LazyColumn עם auto-scroll חכם
- Room DB + ספרייה
- Export: Markdown / PDF
- RTL מלא + עברית

---

## 🧱 מבנה הפרויקט

app/
 ├─ ui/
 │   ├─ clip/
 │   ├─ library/
 │   └─ theme/
 ├─ nav/
 ├─ data/
 │   ├─ db/
 │   └─ repo/
 ├─ transcript/
 ├─ export/
 └─ util/

---

## 🛠️ טכנולוגיות

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room + KSP
- Coroutines
- android-youtube-player

---

## ▶️ Build

./gradlew assembleDebug

---

## ⚠️ הערה חשובה על תמלול

האפליקציה **לא מורידה אודיו או וידאו מיוטיוב**.  
קיים DemoTranscriptProvider לצורכי פיתוח בלבד.

---

## 📜 רישיון

MIT License
