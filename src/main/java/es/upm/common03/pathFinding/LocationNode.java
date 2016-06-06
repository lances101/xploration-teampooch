/*    
    Copyright (C) 2012 http://software-talk.org/ (developer@software-talk.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package es.upm.common03.pathFinding;

import es.upm.common03.LocationUtility;
import es.upm.ontology.Location;

public class LocationNode extends AbstractNode {

    private Location location = new Location();

    public Location getLocation() {
        return location;
    }

    public LocationNode(int xPosition, int yPosition) {
        super(xPosition, yPosition);
        location.setX(xPosition);
        location.setY(yPosition);
    }

    public void sethCosts(AbstractNode endNode) {
        Location end = new Location();
        end.setX(endNode.getxPosition());
        end.setY(endNode.getyPosition());
        int range = LocationUtility.calculateRange(location, end, 10, 10);
        this.sethCosts(range * BASICMOVEMENTCOST);
    }
}
