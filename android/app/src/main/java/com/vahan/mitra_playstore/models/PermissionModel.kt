package com.vahan.mitra_playstore.models

class PermissionModel {
    private var installed_apps: String? = null

    private var calls: String? = null

    private var contacts_sms: String? = null

    private var location: String? = null

    private var notifications: String? = null

    fun getInstalled_apps(): String? {
        return installed_apps
    }

    fun setInstalled_apps(installed_apps: String?) {
        this.installed_apps = installed_apps
    }

    fun getCalls(): String? {
        return calls
    }

    fun setCalls(calls: String?) {
        this.calls = calls
    }

    fun getContacts_sms(): String? {
        return contacts_sms
    }

    fun setContacts_sms(contacts_sms: String?) {
        this.contacts_sms = contacts_sms
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getNotifications(): String? {
        return notifications
    }

    fun setNotifications(notifications: String?) {
        this.notifications = notifications
    }

    override fun toString(): String {
        return "ClassPojo [installed_apps = $installed_apps, calls = $calls, contacts_sms = $contacts_sms, location = $location, notifications = $notifications]"
    }
}