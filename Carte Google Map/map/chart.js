var coord;

function init() 
{
	  var rootApi = 'https://speedy-baton-840.appspot.com/_ah/api'; 
	  gapi.client.load('sictomApi', 'v1', function() 
	  {
		gapi.client.sictomApi.getAllCoords().execute(function(resp) 
		{
		if (!resp.code) 
		{
			var index;
			coord = resp.items;		
		}
	  });
	}, rootApi);
}

google.load("visualization", "1", {packages:["corechart"]});
      // Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(drawChart)

function drawChart()
{
	init();
	var data = new google.visualization.DataTable();
	var data2 = new google.visualization.DataTable();	
	    var tab_coord = new Array();
		var dist_coord = new Array();
		var last_coord_lat = new Array();
		var last_coord_long = new Array();
		//On ajoute dans un tableau associatif les distances parcourue
		for(var i=0; i<coord.length; i++)
		{
			if(!(coord[i].tourneeId in tab_coord))
			{
				//nb coord à 0
				tab_coord[coord[i].tourneeId] = 0;
				//distance à 0
				dist_coord[coord[i].tourneeId] = 0;
			}
			else
			{
				tab_coord[coord[i].tourneeId] += 1;
				//calcul distance
				d = Distance(last_coord_lat[coord[i].tourneeId], last_coord_long[coord[i].tourneeId], coord[i].latitude, coord[i].longitude);
				dist_coord[coord[i].tourneeId] += d;
			}
			//sauvegarde du dernier coord
			last_coord_lat[coord[i].tourneeId] = coord[i].latitude;
			last_coord_long[coord[i].tourneeId] = coord[i].longitude;
		}
        data.addColumn('string', 'Tournee');
        data.addColumn('number', 'nb coordonnees');
		
		data2.addColumn('string', 'Tournee');
        data2.addColumn('number', 'Distance');
       
		for(var tourn in tab_coord)
		{
			data.addRow([tourn, tab_coord[tourn]]);
			data2.addRow([tourn, dist_coord[tourn]]);
		}

        // Set chart options
        var options = {'title':'Nombre coordonnees par tournee',
                       'width':400,
                       'height':300};
					           // Set chart options
        var options2 = {'title':'Distance parcourue par tournee',
                       'width':400,
                       'height':300};
					   
		// Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options);
		var chart2 = new google.visualization.PieChart(document.getElementById('chart_div_2'));
        chart.draw(data2, options2);
}

	function convertRad(input){
        return (Math.PI * input)/180;
	}
 
	function Distance(lat_a_degre, lon_a_degre, lat_b_degre, lon_b_degre){
     
        R = 6378000 //Rayon de la terre en mètre
 
    lat_a = convertRad(lat_a_degre);
    lon_a = convertRad(lon_a_degre);
    lat_b = convertRad(lat_b_degre);
    lon_b = convertRad(lon_b_degre);
     
    d = R * (Math.PI/2 - Math.asin( Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)))
    return d;
	}

 