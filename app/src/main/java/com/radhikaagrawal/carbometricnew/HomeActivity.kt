package com.radhikaagrawal.carbometricnew

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private lateinit var userName: TextView
    private lateinit var userColorCode: View
    private lateinit var emissionsToday: TextView
    private lateinit var emissionsMadeMonth: TextView
    private lateinit var emissionsYear: TextView
    private lateinit var emissionStreak: GridLayout
    private lateinit var barLineChart: CombinedChart

    private val db = FirebaseFirestore.getInstance()
    private val userId = "user_001" // Replace with your actual user id retrieval logic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        backButton = findViewById(R.id.backButton)
        logoutButton = findViewById(R.id.logoutButton)
        userName = findViewById(R.id.userName)
        userColorCode = findViewById(R.id.userColorCode)
        emissionsToday = findViewById(R.id.emissionsToday)
        emissionsMadeMonth = findViewById(R.id.emissionsMadeMonth)
        emissionsYear = findViewById(R.id.emissionsYear)
        emissionStreak = findViewById(R.id.emissionStreak)
        barLineChart = findViewById(R.id.barLineChart)

        backButton.setOnClickListener { finish() }
        logoutButton.setOnClickListener { /* TODO: Implement logout logic */ }

        fetchUserProfile()
        fetchEmissionStats()
        fetchEmissionStreak()
        setupHourlyChart()
    }

    private fun fetchUserProfile() {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "User"
                userName.text = name
                fetchUserColorCode()
            }
            .addOnFailureListener {
                userName.text = "User"
            }
    }

    private fun fetchUserColorCode() {
        db.collection("emissions")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val levelsCount = mutableMapOf("LOW" to 0, "MEDIUM" to 0, "HIGH" to 0)
                for (doc in querySnapshot) {
                    val level = doc.getString("level") ?: "LOW"
                    levelsCount[level] = levelsCount.getOrDefault(level, 0) + 1
                }
                val maxLevel = levelsCount.maxByOrNull { it.value }?.key ?: "LOW"
                val color = when (maxLevel) {
                    "LOW" -> Color.GREEN
                    "MEDIUM" -> Color.YELLOW
                    "HIGH" -> Color.RED
                    else -> Color.GRAY
                }
                userColorCode.setBackgroundColor(color)
            }
            .addOnFailureListener {
                userColorCode.setBackgroundColor(Color.GRAY)
            }
    }

    private fun fetchEmissionStats() {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val todayStart = today.time

        val monthStart = today.clone() as Calendar
        monthStart.set(Calendar.DAY_OF_MONTH, 1)
        val monthStartDate = monthStart.time

        val yearStart = today.clone() as Calendar
        yearStart.set(Calendar.MONTH, Calendar.JANUARY)
        yearStart.set(Calendar.DAY_OF_MONTH, 1)
        val yearStartDate = yearStart.time

        // Today emissions
        db.collection("emissions")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("emissionDate", todayStart)
            .get()
            .addOnSuccessListener { snapshot ->
                emissionsToday.text = "${snapshot.size()} emissions today"
            }
            .addOnFailureListener {
                emissionsToday.text = "0 emissions today"
            }

        // Month emissions MADE
        db.collection("emissions")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("emissionDate", monthStartDate)
            .get()
            .addOnSuccessListener { snapshot ->
                emissionsMadeMonth.text = "${snapshot.size()} emissions made this month"
            }
            .addOnFailureListener {
                emissionsMadeMonth.text = "0 emissions made this month"
            }

        // Year emissions
        db.collection("emissions")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("emissionDate", yearStartDate)
            .get()
            .addOnSuccessListener { snapshot ->
                emissionsYear.text = "${snapshot.size()} emissions this year"
            }
            .addOnFailureListener {
                emissionsYear.text = "0 emissions this year"
            }
    }

    private fun fetchEmissionStreak() {
        val DAYS = 42 // 6 weeks x 7 days
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        // Align to previous Sunday for a calendar-like grid
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK) // 1 (Sunday) to 7 (Saturday)
        val daysToLastSunday = (dayOfWeek - Calendar.SUNDAY + 7) % 7
        today.add(Calendar.DAY_OF_YEAR, -daysToLastSunday)

        // Go back (DAYS-1) days from there
        today.add(Calendar.DAY_OF_YEAR, -(DAYS - 1))
        val startDay = today.time

        // Build list of all days in the streak grid
        val dateList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.time = startDay
        for (i in 0 until DAYS) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        db.collection("emissions")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("emissionDate", dateList.first())
            .whereLessThanOrEqualTo("emissionDate", dateList.last())
            .get()
            .addOnSuccessListener { snapshot ->
                // Map date string (yyyy-MM-dd) to highest emission level for the day
                val dateToLevel = mutableMapOf<String, String>()
                for (doc in snapshot) {
                    val emissionDate = doc.getDate("emissionDate") ?: continue
                    val dayKey = truncateToDay(emissionDate)
                    val level = doc.getString("level") ?: "LOW"
                    // If multiple, prioritize HIGH > MEDIUM > LOW
                    dateToLevel[dayKey] = when {
                        level == "HIGH" -> "HIGH"
                        level == "MEDIUM" && dateToLevel[dayKey] != "HIGH" -> "MEDIUM"
                        level == "LOW" && dateToLevel[dayKey] == null -> "LOW"
                        else -> dateToLevel[dayKey] ?: level
                    }
                }

                emissionStreak.removeAllViews()
                for (date in dateList) {
                    val dayKey = truncateToDay(date)
                    val level = dateToLevel[dayKey]
                    val color = when (level) {
                        "LOW" -> Color.parseColor("#4CAF50")   // green
                        "MEDIUM" -> Color.parseColor("#FFEB3B") // yellow
                        "HIGH" -> Color.parseColor("#F44336")   // red
                        null -> Color.DKGRAY // Gray if no emission
                        else -> Color.DKGRAY
                    }
                    val square = View(this)
                    val params = GridLayout.LayoutParams()
                    params.width = dpToPx(16)
                    params.height = dpToPx(16)
                    params.setMargins(4, 4, 4, 4)
                    square.layoutParams = params
                    square.setBackgroundColor(color)
                    emissionStreak.addView(square)
                }
            }
            .addOnFailureListener {
                emissionStreak.removeAllViews()
            }
    }

    private fun truncateToDay(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return "%04d-%02d-%02d".format(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun setupHourlyChart() {
        // Show emissions in the last 7 hours, including this hour
        val hourLabels = mutableListOf<String>()
        val hourEmissions = mutableListOf<Int>()
        val now = Calendar.getInstance()
        val dbTasks = mutableListOf<com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot>>()
        val hourStartList = mutableListOf<Date>()

        for (i in 6 downTo 0) {
            val cal = now.clone() as Calendar
            cal.add(Calendar.HOUR_OF_DAY, -i)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            hourLabels.add("${cal.get(Calendar.HOUR_OF_DAY)}h")
            hourStartList.add(cal.time)
        }

        // Firestore batch queries for each hour
        for (i in 0 until 7) {
            val hourStart = hourStartList[i]
            val cal = Calendar.getInstance()
            cal.time = hourStart
            cal.add(Calendar.HOUR_OF_DAY, 1)
            val hourEnd = cal.time

            val task = db.collection("emissions")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("emissionDate", hourStart)
                .whereLessThan("emissionDate", hourEnd)
                .get()
            dbTasks.add(task)
        }

        com.google.android.gms.tasks.Tasks.whenAllComplete(dbTasks)
            .addOnSuccessListener {
                hourEmissions.clear()
                for (task in dbTasks) {
                    val result = task.result
                    hourEmissions.add(result?.size() ?: 0)
                }
                populateCombinedChart(hourLabels, hourEmissions)
            }
    }

    private fun populateCombinedChart(hourLabels: List<String>, hourEmissions: List<Int>) {
        val barEntries = mutableListOf<BarEntry>()
        val lineEntries = mutableListOf<Entry>()
        for (i in hourEmissions.indices) {
            barEntries.add(BarEntry(i.toFloat(), hourEmissions[i].toFloat()))
            lineEntries.add(Entry(i.toFloat(), hourEmissions[i].toFloat()))
        }

        val barDataSet = BarDataSet(barEntries, "Emissions").apply {
            color = ContextCompat.getColor(this@HomeActivity, R.color.white)
            valueTextColor = Color.WHITE
        }
        val lineDataSet = LineDataSet(lineEntries, "Trend").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            valueTextColor = Color.WHITE
            lineWidth = 2f
        }

        val combinedData = CombinedData()
        combinedData.setData(BarData(barDataSet))
        combinedData.setData(LineData(lineDataSet))

        barLineChart.data = combinedData
        barLineChart.description.isEnabled = false
        barLineChart.axisLeft.textColor = Color.WHITE
        barLineChart.axisRight.isEnabled = false
        barLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barLineChart.xAxis.textColor = Color.WHITE
        barLineChart.xAxis.setDrawGridLines(false)
        barLineChart.legend.textColor = Color.WHITE
        barLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(hourLabels)
        barLineChart.xAxis.granularity = 1f
        barLineChart.xAxis.labelCount = hourLabels.size
        barLineChart.invalidate()
    }

    // Utility for dp to px conversion
    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}