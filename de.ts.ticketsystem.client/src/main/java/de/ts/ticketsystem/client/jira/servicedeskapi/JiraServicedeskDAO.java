package de.ts.ticketsystem.client.jira.servicedeskapi;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.ts.ticketsystem.client.jira.objects.FieldValue;
import de.ts.ticketsystem.client.jira.objects.Request;

public class JiraServicedeskDAO {

	private WebTarget target;
	private Gson gson;
	private Gson gsonCustomFieldValue;

	/**
	 * Constructs a new instance, which is connecting and communicating with the
	 * given WebTarget.
	 * 
	 * This class expects the authentication to be taken care of.
	 * 
	 * @param target
	 *            Weblocation of the Jira Servicedesk
	 */
	public JiraServicedeskDAO(WebTarget target) {
		this.target = target;
		gson = new Gson();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonCustomFieldValue = gsonBuilder.registerTypeAdapter(FieldValue.class, new FieldValueWriteAdapter()).create();
	}

	/**
	 * Returns the Request for the given id
	 * 
	 * @param issueIdOrKey
	 *            Id or Key of the searched Request
	 * @return
	 */
	public Request getRequestById(String issueIdOrKey) {

		// GET /rest/servicedeskapi/request/{issueIdOrKey}
		Builder builder = target.path("rest").path("servicedeskapi").path("request").path(issueIdOrKey)
				.request(MediaType.APPLICATION_JSON);

		// Mandatory to use the Experimental Jira Service Desk API
		builder.header("X-ExperimentalApi", "opt-in");

		Response response = builder.get();
		String jsonString = response.readEntity(String.class);
		Request jiraRequest = gson.fromJson(jsonString, Request.class);
		return jiraRequest;
	}

	public Request postNewRequest(Request newRestRequest) {
		
		String jsonString = gsonCustomFieldValue.toJson(newRestRequest);

		// POST /rest/servicedeskapi/request
		Builder builder = target.path("rest").path("servicedeskapi").path("request")
				.request(MediaType.APPLICATION_JSON);

		// Mandatory to use the Experimental Jira Service Desk API
		builder.header("X-ExperimentalApi", "opt-in");

		Response response = builder.post(Entity.entity(jsonString, MediaType.APPLICATION_JSON), Response.class);

		System.out.println(response.getStatus());
		jsonString = response.readEntity(String.class);

		Request returnedjiraRequest = gson.fromJson(jsonString, Request.class);
		return returnedjiraRequest;

	}

}
